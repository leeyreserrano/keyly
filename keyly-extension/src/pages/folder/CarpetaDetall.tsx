import React, { useState } from "react"
import { useLocation, useNavigate } from "react-router-dom"

import { carpetasApi } from "~api/carpeta-service"
import { compartitApi, usuarisCompartits } from "~api/compartit-service"
import { itemsApi } from "~api/item-service"
import { userApi } from "~api/user-service"
import { decryptItem, decryptItemWithRawKey } from "~components/ItemCard"
import { TipusEntitat, type CompartitRequest } from "~models/Compartit"

function CarpetaDetall() {
  const { state: carpeta } = useLocation()
  const navigate = useNavigate()
  const [items, setItems] = useState([])
  const [imgErrors, setImgErrors] = useState({})
  const [loaded, setLoaded] = useState(false)
  const [nom, setNom] = useState(carpeta.nom)
  const [editant, setEditant] = useState(false)
  const [modalCompartir, setModalCompartir] = useState(false)
  const [totsElsUsuaris, setTotsElsUsuaris] = useState([])
  const [searchUsuari, setSearchUsuari] = useState("")
  const [usuarisSeleccionats, setUsuarisSeleccionats] = useState<
    { usuari: any; permis: string }[]
  >([])
  const [guardant, setGuardant] = useState(false)

  const handleCompartir = async () => {
    if (usuarisSeleccionats.length === 0) return
    setGuardant(true)
    try {
      const itemsAmbRawKey = await Promise.all(
        carpeta.items.map(async (item) => {
          const itemOriginal = await itemsApi.fetchItem(item.uuid)
          const { rawDataKey } = await decryptItemWithRawKey(itemOriginal)
          return { itemUuid: item.uuid, rawDataKey }
        })
      )

      const usuaris = await usuarisCompartits(
        usuarisSeleccionats.map((u) => u.usuari),
        itemsAmbRawKey
      )

      const usuarisAmbPermis = usuaris.map((u, i) => ({
        ...u,
        permis: usuarisSeleccionats[i].permis
      }))

      const compartitRequest: CompartitRequest = {
        entitatUuid: carpeta.uuid,
        tipusEntitat: TipusEntitat.CARPETA,
        usuaris: usuarisAmbPermis
      }

      await compartitApi.addCompartit(compartitRequest)
      setModalCompartir(false)
      setUsuarisSeleccionats([])
    } catch (err) {
      console.error("Error compartint carpeta:", err)
    } finally {
      setGuardant(false)
    }
  }

  const handleSaveNom = async () => {
    setEditant(false)
    if (nom === carpeta.nom) return
    try {
      await carpetasApi.updateCarpeta({ ...carpeta, nom })
    } catch (err) {
      console.error("Error actualitzant carpeta:", err)
      setNom(carpeta.nom)
    }
  }

  React.useEffect(() => {
    const load = async () => {
      const decrypted = await Promise.all(
        carpeta.items.map(async (item) => {
          if (
            !item.encryptedDataKey ||
            typeof item.encryptedDataKey === "string"
          ) {
            return item
          }
          return decryptItem(item)
        })
      )
      setItems(decrypted)
      setLoaded(true)
    }
    load()
  }, [])
  const getFavicon = (url) => {
    try {
      const domain = new URL(url).hostname
      return `https://icons.duckduckgo.com/ip3/${domain}.ico`
    } catch {
      return null
    }
  }

  const handleFavoriteClick = async (item) => {
    const updated = { ...item, favorit: !item.favorit }
    setItems((prev) => prev.map((i) => (i.uuid === item.uuid ? updated : i)))
    await itemsApi.updateItem(updated)
  }

  return (
    <div className="p-5">
      {/* HEADER */}
      <div className="flex items-center gap-3 mb-5">
        <button onClick={() => navigate(-1)}>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            strokeWidth={1.5}
            stroke="currentColor"
            className="size-6">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M15.75 19.5 8.25 12l7.5-7.5"
            />
          </svg>
        </button>

        <input
          value={nom}
          onChange={(e) => setNom(e.target.value)}
          onFocus={() => setEditant(true)}
          onBlur={handleSaveNom}
          onKeyDown={(e) => e.key === "Enter" && handleSaveNom()}
          className={`text-xl font-bold outline-none bg-transparent border-b-2 transition-colors ${
            editant ? "border-purple-400" : "border-transparent"
          }`}
        />
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          strokeWidth="1.5"
          stroke="currentColor"
          className="size-6 hover:text-gray-600 ml-20 cursor-pointer"
          onClick={() => setModalCompartir(true)}>
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M7.217 10.907a2.25 2.25 0 1 0 0 2.186m0-2.186c.18.324.283.696.283 1.093s-.103.77-.283 1.093m0-2.186 9.566-5.314m-9.566 7.5 9.566 5.314m0 0a2.25 2.25 0 1 0 3.935 2.186 2.25 2.25 0 0 0-3.935-2.186Zm0-12.814a2.25 2.25 0 1 0 3.933-2.185 2.25 2.25 0 0 0-3.933 2.185Z"
          />
        </svg>
      </div>

      {/* ITEMS */}
      {!loaded && <p className="text-sm text-gray-400">Carregant...</p>}

      {loaded && items.length === 0 && (
        <p className="text-sm text-gray-400">Aquesta carpeta està buida</p>
      )}

      {items.map((item) => (
        <span
          key={item.uuid}
          onClick={() => navigate(`/item/${item.uuid}`, { state: item })}
          className="flex items-center gap-3 p-2 border w-full h-16 bg-purple-100 border-purple-300 rounded-lg mb-2 cursor-pointer hover:bg-purple-300 hover:border-purple-400 transition-colors">
          {getFavicon(item.url) && !imgErrors[item.uuid] ? (
            <img
              className="size-6"
              src={getFavicon(item.url)}
              alt=""
              onError={() =>
                setImgErrors((prev) => ({ ...prev, [item.uuid]: true }))
              }
            />
          ) : (
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              strokeWidth={1.5}
              stroke="currentColor"
              className="size-6">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M15.75 5.25a3 3 0 0 1 3 3m3 0a6 6 0 0 1-7.029 5.912c-.563-.097-1.159.026-1.563.43L10.5 17.25H8.25v2.25H6v2.25H2.25v-2.818c0-.597.237-1.17.659-1.591l6.499-6.499c.404-.404.527-1 .43-1.563A6 6 0 1 1 21.75 8.25Z"
              />
            </svg>
          )}

          <div className="flex flex-col min-w-0 flex-1">
            <h1 className="text-lg font-bold truncate">{item.titol}</h1>
            <p className="truncate text-sm text-gray-600">{item.url}</p>
          </div>

          <div className="ml-auto flex gap-2">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill={item.favorit ? "currentColor" : "none"}
              onClick={(e) => {
                e.stopPropagation()
                handleFavoriteClick(item)
              }}
              viewBox="0 0 24 24"
              strokeWidth={1.5}
              stroke={item.favorit ? "#facc15" : "currentColor"}
              className={`size-6 hover:cursor-pointer transition-colors ${
                item.favorit
                  ? "text-yellow-400 hover:text-yellow-600"
                  : "hover:text-yellow-400"
              }`}>
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M11.48 3.499a.562.562 0 0 1 1.04 0l2.125 5.111a.563.563 0 0 0 .475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 0 0-.182.557l1.285 5.385a.562.562 0 0 1-.84.61l-4.725-2.885a.562.562 0 0 0-.586 0L6.982 20.54a.562.562 0 0 1-.84-.61l1.285-5.386a.562.562 0 0 0-.182-.557l-4.204-3.602a.562.562 0 0 1 .321-.988l5.518-.442a.563.563 0 0 0 .475-.345L11.48 3.5Z"
              />
            </svg>

            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              strokeWidth={1.5}
              stroke="currentColor"
              className="size-6 hover:cursor-pointer hover:text-purple-900 transition-colors"
              onClick={(e) => {
                e.stopPropagation()
                navigate(`/item/edit/${item.uuid}`, {
                  state: { ...item, from: `/carpeta/${carpeta.uuid}` }
                })
              }}>
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10"
              />
            </svg>
          </div>
        </span>
      ))}
      {modalCompartir && (
        <div
          className="fixed inset-0 bg-black/40 z-50 flex items-center justify-center"
          onClick={() => setModalCompartir(false)}>
          <div
            className="bg-white rounded-2xl shadow-xl w-[380px] p-6 space-y-4"
            onClick={(e) => e.stopPropagation()}>
            {/* Títol */}
            <div className="flex items-center justify-between border-b pb-3">
              <h2 className="text-lg font-bold">Compartir "{nom}"</h2>
              <button onClick={() => setModalCompartir(false)}>
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

            {/* Usuaris seleccionats */}
            {usuarisSeleccionats.length > 0 && (
              <div className="space-y-1">
                {usuarisSeleccionats.map(({ usuari, permis }) => (
                  <div
                    key={usuari.uuid}
                    className="flex items-center justify-between border rounded-lg px-3 py-2">
                    <div className="flex items-center gap-2">
                      <div className="size-7 rounded-full bg-purple-100 flex items-center justify-center text-xs font-bold text-purple-700">
                        {usuari.nom.charAt(0).toUpperCase()}
                      </div>
                      <div>
                        <p className="text-sm font-medium">{usuari.nom}</p>
                        <p className="text-xs text-gray-400">{usuari.correu}</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <select
                        value={permis}
                        onChange={(e) =>
                          setUsuarisSeleccionats((prev) =>
                            prev.map((u) =>
                              u.usuari.uuid === usuari.uuid
                                ? { ...u, permis: e.target.value }
                                : u
                            )
                          )
                        }
                        className="text-xs border rounded px-1 py-1 outline-none">
                        <option value="LECTURA">Lectura</option>
                        <option value="ESCRIPTURA">Escriptura</option>
                        <option value="ADMINISTRADOR">Administrador</option>
                      </select>
                      <button
                        onClick={() =>
                          setUsuarisSeleccionats((prev) =>
                            prev.filter((u) => u.usuari.uuid !== usuari.uuid)
                          )
                        }
                        className="text-red-400 hover:text-red-600">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          fill="none"
                          viewBox="0 0 24 24"
                          strokeWidth={1.5}
                          stroke="currentColor"
                          className="size-4">
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            d="M6 18 18 6M6 6l12 12"
                          />
                        </svg>
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* Buscador */}
            <div className="relative">
              <input
                value={searchUsuari}
                onChange={(e) => setSearchUsuari(e.target.value)}
                placeholder="Afegir usuari..."
                className="w-full border rounded-lg px-3 py-2 outline-none text-sm"
              />
              {searchUsuari && (
                <div className="absolute z-10 w-full bg-white border rounded-lg shadow mt-1 max-h-40 overflow-y-auto">
                  {totsElsUsuaris
                    .filter(
                      (u) =>
                        !usuarisSeleccionats.some(
                          (s) => s.usuari.uuid === u.uuid
                        ) &&
                        (u.nom
                          .toLowerCase()
                          .includes(searchUsuari.toLowerCase()) ||
                          u.correu
                            .toLowerCase()
                            .includes(searchUsuari.toLowerCase()))
                    )
                    .map((u) => (
                      <div
                        key={u.uuid}
                        onClick={() => {
                          setUsuarisSeleccionats((prev) => [
                            ...prev,
                            { usuari: u, permis: "LECTURA" }
                          ])
                          setSearchUsuari("")
                        }}
                        className="flex items-center gap-2 px-3 py-2 hover:bg-purple-50 cursor-pointer">
                        <div className="size-7 rounded-full bg-gray-100 flex items-center justify-center text-xs font-bold text-gray-500">
                          {u.nom.charAt(0).toUpperCase()}
                        </div>
                        <div>
                          <p className="text-sm font-medium">{u.nom}</p>
                          <p className="text-xs text-gray-400">{u.correu}</p>
                        </div>
                      </div>
                    ))}
                  {totsElsUsuaris.filter(
                    (u) =>
                      !usuarisSeleccionats.some(
                        (s) => s.usuari.uuid === u.uuid
                      ) &&
                      (u.nom
                        .toLowerCase()
                        .includes(searchUsuari.toLowerCase()) ||
                        u.correu
                          .toLowerCase()
                          .includes(searchUsuari.toLowerCase()))
                  ).length === 0 && (
                    <div className="px-3 py-2 text-sm text-gray-400">
                      No s'han trobat usuaris
                    </div>
                  )}
                </div>
              )}
            </div>

            {/* Botó compartir */}
            <button
              onClick={handleCompartir}
              disabled={usuarisSeleccionats.length === 0 || guardant}
              className="w-full bg-purple-500 text-white py-2 rounded-lg hover:bg-purple-600 disabled:opacity-50 disabled:cursor-not-allowed">
              {guardant ? "Compartint..." : "Compartir"}
            </button>
          </div>
        </div>
      )}
    </div>
  )
}

export default CarpetaDetall
