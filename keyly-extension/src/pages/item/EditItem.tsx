import React, { useEffect, useState } from "react"
import { useLocation, useNavigate } from "react-router-dom"

import { carpetasApi } from "~api/carpeta-service"
import { compartitApi, usuarisCompartits } from "~api/compartit-service"
import { itemsApi } from "~api/item-service"
import { userApi } from "~api/user-service"
import { decryptItemWithRawKey } from "~components/ItemCard"
import { TipusEntitat, type CompartitRequest } from "~models/Compartit"
import { encryptItemPassword } from "~utils/crypto-utils"

function EditItem() {
  const navigate = useNavigate()
  const [itemOriginal, setItemOriginal] = useState(null)
  const { state: item } = useLocation()
  const [showPassword, setShowPassword] = useState(false)
  const [usuarisAmbAcces, setUsuarisAmbAcces] = useState([])
  const [totsElsUsuaris, setTotsElsUsuaris] = useState([])
  const [searchUsuari, setSearchUsuari] = useState("")
  const [rawDataKey, setRawDataKey] = useState<ArrayBuffer | null>(null)
  const [usuarisAAfegir, setUsuarisAAfegir] = useState([])
  const [usuarisAEliminar, setUsuarisAEliminar] = useState([])

  const [carpetes, setCarpetes] = useState([])
  const [carpetaActual, setCarpetaActual] = useState(null)

  const [formData, setFormData] = useState({
    titol: item.titol || "",
    url: item.url || "",
    contrasenya: item.contrasenya || "",
    notes: item.notes || "",
    favorit: item.favorit || false,
    carpetaUuid: null
  })

  useEffect(() => {
    const loadCarpetes = async () => {
      try {
        const data = await carpetasApi.fetchCarpetas()
        setCarpetes(data)

        const carpetaConItem = data.find((c) =>
          c.items.some((i) => i.uuid === item.uuid)
        )

        if (carpetaConItem) {
          setCarpetaActual(carpetaConItem.uuid)
          setFormData((prev) => ({ ...prev, carpetaUuid: carpetaConItem.uuid }))
        }
      } catch (error) {
        console.error("Error carregant carpetes:", error)
      }
    }
    const loadDades = async () => {
      try {
        const itemOriginalRes = await itemsApi.fetchItem(item.uuid)
        setItemOriginal(itemOriginalRes)
        const { rawDataKey: rdk } = await decryptItemWithRawKey(itemOriginalRes)
        setRawDataKey(rdk)

        const compartitsRes = await compartitApi.fetchCompartitsCreats()
        const filtrats = compartitsRes.filter(
          (c) => c.tipusEntitat === "ITEM" && c.item?.uuid === item.uuid
        )
        setUsuarisAmbAcces(filtrats)

        const usuaris = await userApi.fetchUsers()
        console.log("usuaris:", usuaris)
        setTotsElsUsuaris(usuaris)
      } catch (error) {
        console.error("Error a loadDades:", error)
      }
    }

    loadDades()
    loadCarpetes()
  }, [])

  const handleAfegirUsuari = (usuari) => {
    setUsuarisAmbAcces((prev) => [
      ...prev,
      { uuid: `temp-${usuari.uuid}`, usuariReceptor: usuari }
    ])
    setUsuarisAAfegir((prev) => [...prev, usuari])
    setSearchUsuari("")
  }

  const handleEliminarAcces = (compartitUuid, usuariUuid) => {
    setUsuarisAmbAcces((prev) => prev.filter((c) => c.uuid !== compartitUuid))

    if (compartitUuid.startsWith("temp-")) {
      // Era un usuario recién añadido, solo quitarlo de usuarisAAfegir
      setUsuarisAAfegir((prev) => prev.filter((u) => u.uuid !== usuariUuid))
    } else {
      // Era un compartit real, marcar para borrar al guardar
      setUsuarisAEliminar((prev) => [...prev, compartitUuid])
    }
  }

  const uuidsAmbAcces = usuarisAmbAcces.map((c) => c.usuariReceptor.uuid)
  const usuarisFiltrats = totsElsUsuaris.filter(
    (u) =>
      !uuidsAmbAcces.includes(u.uuid) &&
      (u.nom.toLowerCase().includes(searchUsuari.toLowerCase()) ||
        u.correu.toLowerCase().includes(searchUsuari.toLowerCase()))
  )

  const handleChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }))
  }

  const handleSubmit = async () => {
    try {
      const { contrasenyaEncriptada, ivB64 } = await encryptItemPassword(
        formData.contrasenya,
        rawDataKey
      )

      await itemsApi.updateItem({
        ...itemOriginal,
        ...formData,
        contrasenya: contrasenyaEncriptada,
        iv: ivB64,
        encryptedDataKey: itemOriginal.encryptedDataKey.encryptedDatakey
      })

      // Si la carpeta ha cambiado, mover el item
      if (formData.carpetaUuid && formData.carpetaUuid !== carpetaActual) {
        await carpetasApi.addItemInCarpeta(formData.carpetaUuid, item.uuid)
      }

      await Promise.all(
        usuarisAEliminar.map((uuid) => compartitApi.deleteCompartit(uuid))
      )

      if (usuarisAAfegir.length > 0) {
        const usuaris = await usuarisCompartits(usuarisAAfegir, rawDataKey)
        const compartit: CompartitRequest = {
          entitatUuid: item.uuid,
          tipusEntitat: TipusEntitat.ITEM,
          usuaris
        }
        await compartitApi.addCompartit(compartit)
      }

      navigate("/home")
    } catch (err) {
      console.error("Error guardant:", err)
    }
  }

  const onSave = (data) => {}

  const carpetesOrdenades = carpetaActual
    ? [
        carpetes.find((c) => c.uuid === carpetaActual),
        ...carpetes.filter((c) => c.uuid !== carpetaActual)
      ].filter(Boolean)
    : carpetes

  return (
    <div className="relative flex flex-col min-h-screen mb-16">
      <div className="flex flex-col items-center flex-1 pt-2 bg-gray-50">
        <div className="w-[400px] bg-white rounded-2xl shadow-md border p-6 space-y-5 mb-56">
          {/* HEADER */}
          <div className="text-center border-b pb-4">
            <div className="flex items-center">
              <input
                value={formData.titol}
                onChange={(e) => handleChange("titol", e.target.value)}
                className="text-2xl font-semibold text-center w-full outline-none"
                placeholder="Títol"
              />
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill={formData.favorit ? "currentColor" : "none"}
                onClick={() => handleChange("favorit", !formData.favorit)}
                viewBox="0 0 24 24"
                strokeWidth={1.5}
                stroke={formData.favorit ? "#facc15" : "currentColor"}
                className={`size-6 hover:cursor-pointer transition-colors ${
                  formData.favorit
                    ? "text-yellow-400 hover:text-yellow-600"
                    : "hover:text-yellow-400"
                }`}>
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M11.48 3.499a.562.562 0 0 1 1.04 0l2.125 5.111a.563.563 0 0 0 .475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 0 0-.182.557l1.285 5.385a.562.562 0 0 1-.84.61l-4.725-2.885a.562.562 0 0 0-.586 0L6.982 20.54a.562.562 0 0 1-.84-.61l1.285-5.386a.562.562 0 0 0-.182-.557l-4.204-3.602a.562.562 0 0 1 .321-.988l5.518-.442a.563.563 0 0 0 .475-.345L11.48 3.5Z"
                />
              </svg>
            </div>
          </div>
          {/* PASSWORD */}
          <div>
            <p className="text-xs text-gray-500 mb-1">Contrasenya</p>
            <div className="flex items-center gap-2 border rounded-lg px-3 py-2">
              <input
                type={showPassword ? "text" : "password"}
                value={showPassword ? formData.contrasenya : "******"}
                onChange={(e) => handleChange("contrasenya", e.target.value)}
                className="flex-1 outline-none bg-transparent"
              />
              <button onClick={() => setShowPassword(!showPassword)}>
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
                onClick={() =>
                  navigator.clipboard.writeText(formData.contrasenya)
                }>
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
                    d="M9 12h3.75M9 15h3.75M9 18h3.75m3 .75H18a2.25 2.25 0 0 0 2.25-2.25V6.108c0-1.135-.845-2.098-1.976-2.192a48.424 48.424 0 0 0-1.123-.08m-5.801 0c-.065.21-.1.433-.1.664 0 .414.336.75.75.75h4.5a.75.75 0 0 0 .75-.75 2.25 2.25 0 0 0-.1-.664m-5.8 0A2.251 2.251 0 0 1 13.5 2.25H15c1.012 0 1.867.668 2.15 1.586m-5.8 0c-.376.023-.75.05-1.124.08C9.095 4.01 8.25 4.973 8.25 6.108V8.25m0 0H4.875c-.621 0-1.125.504-1.125 1.125v11.25c0 .621.504 1.125 1.125 1.125h9.75c.621 0 1.125-.504 1.125-1.125V9.375c0-.621-.504-1.125-1.125-1.125H8.25ZM6.75 12h.008v.008H6.75V12Zm0 3h.008v.008H6.75V15Zm0 3h.008v.008H6.75V18Z"
                  />
                </svg>
              </button>
            </div>
          </div>
          {/* URL */}
          <div>
            <p className="text-xs text-gray-500">URL</p>
            <input
              value={formData.url}
              onChange={(e) => handleChange("url", e.target.value)}
              className="w-full border rounded-lg px-3 py-2 outline-none"
              placeholder="https://..."
            />
          </div>
          {/* NOTES */}
          <div>
            <p className="text-xs text-gray-500">Notes</p>
            <textarea
              value={formData.notes}
              onChange={(e) => handleChange("notes", e.target.value)}
              className="w-full border rounded-lg px-3 py-2 outline-none"
              rows={3}
            />
          </div>
          {/* CARPETA */}
          <div>
            <p className="text-xs text-gray-500 mb-1">Carpeta</p>
            <select
              value={formData.carpetaUuid || ""}
              onChange={(e) =>
                handleChange("carpetaUuid", e.target.value || null)
              }
              className="w-full border rounded-lg px-3 py-2 outline-none bg-white">
              <option value="">Sense carpeta</option>
              {carpetesOrdenades.map((c) => (
                <option key={c.uuid} value={c.uuid}>
                  {c.nom}
                  {c.uuid === carpetaActual ? " (actual)" : ""}
                </option>
              ))}
            </select>
          </div>
          {/* COMPARTIR */}
          <div>
            <p className="text-xs text-gray-500 mb-2">Compartit amb</p>

            {/* Usuarios que ya tienen acceso */}
            <div className="space-y-1 mb-3">
              {usuarisAmbAcces.length === 0 && (
                <p className="text-xs text-gray-400">
                  Ningú té accés a aquest item
                </p>
              )}
              {usuarisAmbAcces.map((c) => (
                <div
                  key={c.uuid}
                  className="flex items-center justify-between border rounded-lg px-3 py-2">
                  <div>
                    <p className="text-sm font-medium">
                      {c.usuariReceptor.nom}
                    </p>
                    <p className="text-xs text-gray-400">
                      {c.usuariReceptor.correu}
                    </p>
                  </div>
                  <button
                    onClick={() =>
                      handleEliminarAcces(c.uuid, c.usuariReceptor.uuid)
                    }
                    className="text-red-400 hover:text-red-600 transition-colors">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      strokeWidth={1.5}
                      stroke="currentColor"
                      className="size-5">
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="M6 18 18 6M6 6l12 12"
                      />
                    </svg>
                  </button>
                </div>
              ))}
            </div>

            {/* Buscador para añadir — AQUÍ dentro del card */}
            <div className="relative">
              <input
                value={searchUsuari}
                onChange={(e) => setSearchUsuari(e.target.value)}
                placeholder="Afegir usuari..."
                className="w-full border rounded-lg px-3 py-2 outline-none text-sm"
              />
              {searchUsuari && usuarisFiltrats.length > 0 && (
                <div className="absolute z-10 w-full bg-white border rounded-lg shadow mt-1 max-h-40 overflow-y-auto">
                  {usuarisFiltrats.map((u) => (
                    <div
                      key={u.uuid}
                      onClick={() => {
                        handleAfegirUsuari(u)
                        setSearchUsuari("")
                      }}
                      className="flex items-center gap-2 px-3 py-2 hover:bg-purple-50 cursor-pointer">
                      <div>
                        <p className="text-sm font-medium">{u.nom}</p>
                        <p className="text-xs text-gray-400">{u.correu}</p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
              {searchUsuari && usuarisFiltrats.length === 0 && (
                <div className="absolute z-10 w-full bg-white border rounded-lg shadow mt-1 px-3 py-2 text-sm text-gray-400">
                  No s'han trobat usuaris
                </div>
              )}
            </div>
          </div>
        </div>

        {/* ACTIONS */}
        <div className="fixed bottom-12 left-1/2 -translate-x-1/2 flex gap-3 z-50">
          <button
            onClick={handleSubmit}
            className="bg-purple-500 text-white py-2 px-10 rounded-lg hover:bg-purple-600">
            Guardar
          </button>
          <button
            onClick={() => navigate("/home")}
            className="border py-2 px-10 rounded-lg hover:bg-gray-100">
            Cancelar
          </button>
        </div>
      </div>
    </div>
  )
}

export default EditItem
