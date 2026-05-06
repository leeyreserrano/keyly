import React, { useState } from "react"
import { useLocation, useNavigate } from "react-router-dom"
import { carpetasApi } from "~api/carpeta-service"

import { itemsApi } from "~api/item-service"
import { decryptItem } from "~components/ItemCard"

function CarpetaDetall() {
  const { state: carpeta } = useLocation()
  const navigate = useNavigate()
  const [items, setItems] = useState([])
  const [imgErrors, setImgErrors] = useState({})
  const [loaded, setLoaded] = useState(false)
  const [nom, setNom] = useState(carpeta.nom)
  const [editant, setEditant] = useState(false)

  const handleSaveNom = async () => {
    setEditant(false)
    if (nom === carpeta.nom) return
    try {
      await carpetasApi.updateCarpeta({ ...carpeta, nom })
    } catch (err) {
      console.error("Error actualitzant carpeta:", err)
      setNom(carpeta.nom) // revertir si falla
    }
  }

  // Desencriptar los items al montar
  React.useEffect(() => {
    const load = async () => {
      const decrypted = await Promise.all(carpeta.items.map(decryptItem))
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
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
            strokeWidth={1.5} stroke="currentColor" className="size-6">
            <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 19.5 8.25 12l7.5-7.5" />
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
    </div>
  )
}

export default CarpetaDetall
