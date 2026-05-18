import React, { useEffect, useRef, useState } from "react"
import { useLocation, useNavigate } from "react-router-dom"

import { getPublicKey, getStoredKey } from "~api/auth-service"
import { carpetasApi } from "~api/carpeta-service"
import { compartitApi, usuarisCompartits } from "~api/compartit-service"
import { itemsApi } from "~api/item-service"
import { userApi } from "~api/user-service"
import { utilsService } from "~api/utils-service"
import type { Carpeta } from "~models/Carpeta"
import { TipusEntitat, type CompartitRequest } from "~models/Compartit"
import type { CustomPassword } from "~models/CustomPassword"
import type { Item, ItemResponse } from "~models/Item"
import { type User } from "~models/User"

function bytesToBase64(bytes) {
  return btoa(String.fromCharCode(...new Uint8Array(bytes)))
}

function NewItem() {
  const [showPassword, setShowPassword] = useState(false)
  const [showMenu, setShowMenu] = useState(false)
  const [open, setOpen] = useState(false)
  const [carpetas, setCarpetas] = useState<Carpeta[]>([])
  const [users, setUsers] = useState<User[]>([])
  const menuRef = useRef(null)
  const navigate = useNavigate()
  const [titol, setTitol] = useState("")
  const [nomUsuari, setNomUsuari] = useState("")
  const [url, setUrl] = useState("")
  const [contrasenya, setContrasenya] = useState("")
  const [notes, setNotes] = useState("")
  const [carpeta, setCarpeta] = useState("")
  const [compartir, setCompartir] = useState([])
  const location = useLocation()

  const from = location.state?.from || "/home"

  useEffect(() => {
    const loadCarpeta = async () => {
      try {
        const [carpetasData] = await Promise.all([carpetasApi.fetchCarpetas()])
        const [userData] = await Promise.all([userApi.fetchUsers()])
        setCarpetas(carpetasData)
        setUsers(userData)
      } catch (error) {
        console.log(error)
      }
    }

    function handleClickOutside(event) {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setShowMenu(false)
      }
    }
    loadCarpeta()

    document.addEventListener("mousedown", handleClickOutside)
    return () => {
      document.removeEventListener("mousedown", handleClickOutside)
    }
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    const iv = crypto.getRandomValues(new Uint8Array(12))

    const dataKey = await crypto.subtle.generateKey(
      {
        name: "AES-GCM",
        length: 256
      },
      true,
      ["encrypt", "decrypt"]
    )

    const rawDataKey = await crypto.subtle.exportKey("raw", dataKey)

    const encryptedDataKey = await crypto.subtle.encrypt(
      { name: "RSA-OAEP" },
      await getPublicKey(),
      rawDataKey
    )

    const data = new TextEncoder().encode(contrasenya)

    const encrypted = await crypto.subtle.encrypt(
      { name: "AES-GCM", iv },
      dataKey,
      data
    )
    const item: Item = {
      uuid: null,
      titol: titol,
      nomUsuari: nomUsuari,
      contrasenya: bytesToBase64(encrypted),
      iv: bytesToBase64(iv),
      encryptedDataKey: bytesToBase64(encryptedDataKey),
      url: url,
      notes: notes,
      favorit: false,
      dataCreacio: null,
      dataEditat: null,
      ultimAccess: null,
      dinsDeCarpeta: carpeta.length > 0
    }

    console.log("Super item " + item)

    if (compartir.length > 0 && carpeta.length > 0) {
      const i: ItemResponse = await carpetasApi.createNewItemInCarpeta(
        carpeta,
        item
      )

      const selectedUsers = users.filter((u) => compartir.includes(u.uuid))

      const usuaris = await usuarisCompartits(selectedUsers, [
        { itemUuid: i.uuid, rawDataKey }
      ])

      const compartit: CompartitRequest = {
        entitatUuid: i.uuid,
        tipusEntitat: TipusEntitat.ITEM,
        usuaris
      }

      await compartitApi.addCompartit(compartit)
    } else if (compartir.length > 0) {
      const selectedUsers = users.filter((u) => compartir.includes(u.uuid))
      await compartitApi.newItemCompartir(selectedUsers, item, rawDataKey)
    } else if (carpeta.length > 0) {
      await carpetasApi.createNewItemInCarpeta(carpeta, item)
    } else {
      await itemsApi.saveItem(item)
    }

    navigate(from)
  }

  const customPasswordLow = async () => {
    const passwordConfig: CustomPassword = {
      longitud: 8,
      may: true,
      quantitatMay: 2,
      numeros: false,
      quantitatNumeros: 0,
      caractersEspecials: false,
      quantitatCaractersEspecials: 0
    }

    customPassword(passwordConfig)
  }

  const customPasswordMid = async () => {
    const passwordConfig: CustomPassword = {
      longitud: 12,
      may: true,
      quantitatMay: 4,
      numeros: true,
      quantitatNumeros: 3,
      caractersEspecials: true,
      quantitatCaractersEspecials: 1
    }

    customPassword(passwordConfig)
  }

  const customPasswordHigh = async () => {
    const passwordConfig: CustomPassword = {
      longitud: 15,
      may: true,
      quantitatMay: 4,
      numeros: true,
      quantitatNumeros: 3,
      caractersEspecials: true,
      quantitatCaractersEspecials: 4
    }

    customPassword(passwordConfig)
  }

  const customPassword = async (passwordConfig: CustomPassword) => {
    try {
      const result = await utilsService.customPassword(passwordConfig)
      setContrasenya(result)
    } catch (error) {
      console.error(error)
    }
  }

  return (
    <div className="relative flex flex-col flex-1 h-full w-full overflow-hidden">
      <form
        className="flex flex-col items-center gap-5 flex-1 overflow-y-auto pb-16 pt-5"
        id="newItem"
        action=""
        onSubmit={handleSubmit}>
        <fieldset>
          <legend className="text-lg font-bold">Creació d'un nou item</legend>
        </fieldset>
        <div className="relative w-80">
          <input
            type="text"
            value={titol}
            onChange={(e) => setTitol(e.target.value)}
            required
            className="w-full border border-gray-400 rounded-lg p-2 pt-4 focus:outline-none focus:ring-2 focus:ring-purple-200"
          />
          <label className="absolute left-3 -top-2 bg-white px-1 text-sm text-gray-600">
            Titol *
          </label>
        </div>

        <div className="relative w-80">
          <input
            type="text"
            value={nomUsuari}
            onChange={(e) => setNomUsuari(e.target.value)}
            required
            className="w-full border border-gray-400 rounded-lg p-2 pt-4 focus:outline-none focus:ring-2 focus:ring-purple-200"
          />
          <label className="absolute left-3 -top-2 bg-white px-1 text-sm text-gray-600">
            Nom Usuari *
          </label>
        </div>

        <div className="relative w-80">
          <input
            type="text"
            value={url}
            onChange={(e) => setUrl(e.target.value)}
            className="w-full border border-gray-400 rounded-lg p-2 pt-4 focus:outline-none focus:ring-2 focus:ring-purple-200"
          />
          <label className="absolute left-3 -top-2 bg-white px-1 text-sm text-gray-600">
            Url
          </label>
        </div>

        <div className="relative w-80">
          <input
            type={showPassword ? "text" : "password"}
            value={contrasenya}
            onChange={(e) => setContrasenya(e.target.value)}
            required
            className="w-full border border-gray-400 rounded-lg p-2 pt-4 pr-24 focus:outline-none focus:ring-2 focus:ring-purple-200"
          />

          <label className="absolute left-3 -top-2 bg-white px-1 text-sm text-gray-600">
            Contrasenya *
          </label>

          <div className="absolute right-2 top-1/2 -translate-y-1/2 flex gap-2">
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="text-gray-600 hover:text-gray-900">
              {showPassword ? (
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="1.5"
                  stroke="currentColor"
                  className="size-6">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M3.98 8.223A10.477 10.477 0 0 0 1.934 12C3.226 16.338 7.244 19.5 12 19.5c.993 0 1.953-.138 2.863-.395M6.228 6.228A10.451 10.451 0 0 1 12 4.5c4.756 0 8.773 3.162 10.065 7.498a10.522 10.522 0 0 1-4.293 5.774M6.228 6.228 3 3m3.228 3.228 3.65 3.65m7.894 7.894L21 21m-3.228-3.228-3.65-3.65m0 0a3 3 0 1 0-4.243-4.243m4.242 4.242L9.88 9.88"
                  />
                </svg>
              ) : (
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="1.5"
                  stroke="currentColor"
                  className="size-6">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M2.036 12.322a1.012 1.012 0 0 1 0-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178Z"
                  />
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"
                  />
                </svg>
              )}
            </button>

            <button
              onClick={() => setShowMenu(!showMenu)}
              type="button"
              className="text-gray-600 hover:text-gray-900">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
                className="size-6 transition-transform hover:rotate-45 duration-500">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M9.594 3.94c.09-.542.56-.94 1.11-.94h2.593c.55 0 1.02.398 1.11.94l.213 1.281c.063.374.313.686.645.87.074.04.147.083.22.127.325.196.72.257 1.075.124l1.217-.456a1.125 1.125 0 0 1 1.37.49l1.296 2.247a1.125 1.125 0 0 1-.26 1.431l-1.003.827c-.293.241-.438.613-.43.992a7.723 7.723 0 0 1 0 .255c-.008.378.137.75.43.991l1.004.827c.424.35.534.955.26 1.43l-1.298 2.247a1.125 1.125 0 0 1-1.369.491l-1.217-.456c-.355-.133-.75-.072-1.076.124a6.47 6.47 0 0 1-.22.128c-.331.183-.581.495-.644.869l-.213 1.281c-.09.543-.56.94-1.11.94h-2.594c-.55 0-1.019-.398-1.11-.94l-.213-1.281c-.062-.374-.312-.686-.644-.87a6.52 6.52 0 0 1-.22-.127c-.325-.196-.72-.257-1.076-.124l-1.217.456a1.125 1.125 0 0 1-1.369-.49l-1.297-2.247a1.125 1.125 0 0 1 .26-1.431l1.004-.827c.292-.24.437-.613.43-.991a6.932 6.932 0 0 1 0-.255c.007-.38-.138-.751-.43-.992l-1.004-.827a1.125 1.125 0 0 1-.26-1.43l1.297-2.247a1.125 1.125 0 0 1 1.37-.491l1.216.456c.356.133.751.072 1.076-.124.072-.044.146-.086.22-.128.332-.183.582-.495.644-.869l.214-1.28Z"
                />
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"
                />
              </svg>
            </button>
          </div>
          {showMenu && (
            <div
              ref={menuRef}
              className="absolute -right-5 -mt-2 bg-white border rounded-lg shadow p-2 z-20 text-sm">
              <div className="flex items-center justify-center w-30 hover:bg-gray-100 cursor-pointer rounded-lg">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="1.5"
                  stroke="currentColor"
                  className="size-6">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M5 12h14"
                  />
                </svg>

                <button
                  onClick={customPasswordLow}
                  className="block w-full text-left p-2">
                  Baixa
                </button>
              </div>

              <hr />

              <div className="flex items-center justify-center hover:bg-gray-100 cursor-pointer rounded-lg">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="1.5"
                  stroke="currentColor"
                  className="size-6">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M12 4.5v15m7.5-7.5h-15"
                  />
                </svg>

                <button
                  onClick={customPasswordMid}
                  className="block w-full text-left p-2">
                  Mitja
                </button>
              </div>

              <hr />

              <div className="flex items-center justify-center hover:bg-gray-100 cursor-pointer rounded-lg">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="currentColor"
                  className="size-4 ml-[3] mr-[4]"
                  viewBox="0 0 16 16">
                  <path d="M8 0a1 1 0 0 1 1 1v5.268l4.562-2.634a1 1 0 1 1 1 1.732L10 8l4.562 2.634a1 1 0 1 1-1 1.732L9 9.732V15a1 1 0 1 1-2 0V9.732l-4.562 2.634a1 1 0 1 1-1-1.732L6 8 1.438 5.366a1 1 0 0 1 1-1.732L7 6.268V1a1 1 0 0 1 1-1" />
                </svg>
                <button
                  onClick={customPasswordHigh}
                  className="block w-full text-left p-2">
                  Alta
                </button>
              </div>
            </div>
          )}
        </div>

        <div className="relative w-80">
          <textarea
            rows={2}
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            className="w-full border border-gray-400 rounded-lg p-2 pt-6 focus:outline-none focus:ring-2 focus:ring-purple-200 resize-none"
          />

          <label className="absolute left-3 -top-2 bg-white px-1 text-sm text-gray-600">
            Notes
          </label>
        </div>

        <div className="relative w-80">
          <label className="absolute left-3 -top-2 bg-white px-1 text-sm text-gray-600">
            Carpeta
          </label>

          <select
            value={carpeta}
            onChange={(e) => setCarpeta(e.target.value)}
            onMouseDown={() => setOpen(true)}
            onBlur={() => setOpen(false)}
            className="w-full border border-gray-400 rounded-lg bg-transparent p-2 pt-4 pr-10 appearance-none focus:outline-none focus:ring-2 focus:ring-purple-200">
            <option value=""></option>

            {carpetas.map((c) => (
              <option key={c.uuid} value={c.uuid}>
                {c.nom}
              </option>
            ))}
          </select>

          <div
            className={`absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-gray-500 transition-transform duration-200 ${
              open ? "rotate-180" : ""
            }`}>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              strokeWidth={1.5}
              stroke="currentColor"
              className="w-4 h-4">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="m19.5 8.25-7.5 7.5-7.5-7.5"
              />
            </svg>
          </div>
        </div>

        <div className="relative w-80">
          <label className="absolute left-3 -top-2 bg-white px-1 text-sm text-gray-600">
            Compartir
          </label>

          <select
            multiple
            value={compartir}
            onChange={(e) => {
              const values = Array.from(
                e.target.selectedOptions,
                (option) => option.value
              )
              setCompartir(values)
            }}
            className="w-full border border-gray-400 rounded-lg p-2 pt-4">
            {" "}
            {users.map((user) => (
              <option key={user.uuid} value={user.uuid}>
                {user.correu}
              </option>
            ))}
          </select>
        </div>
      </form>
      <div className="absolute bottom-3 left-1/2 -translate-x-1/2 flex gap-3 pt-4">
        <button
          type="submit"
          form="newItem"
          className="bg-purple-500 text-white py-2 px-10 rounded-lg hover:bg-purple-600">
          Guardar
        </button>
        <button
          onClick={() => navigate(from)}
          className="border py-2 px-10 rounded-lg hover:bg-gray-100">
          Cancelar
        </button>
      </div>
    </div>
  )
}

export default NewItem
