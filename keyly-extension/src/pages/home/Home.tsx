import React, { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { carpetasApi } from "src/api/carpeta-service"
import { itemsApi } from "src/api/item-service"

import { compartitApi } from "~api/compartit-service"
import type { Carpeta } from "~models/Carpeta"
import { TipusEntitat, type Compartit } from "~models/Compartit"
import type { Item } from "~models/Item"

function Home() {
  const [items, setItems] = useState<Item[]>([])
  const [carpetas, setCarpetas] = useState<Carpeta[]>([])
  const [compartits, setCompartits] = useState<Compartit[]>([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState("")
  const [imgErrors, setImgErrors] = useState({})
  const navigate = useNavigate()

  useEffect(() => {
    const loadData = async () => {
      try {
        const [itemsData, carpetasData, compartitsData] = await Promise.all([
          itemsApi.fetchItems(),
          carpetasApi.fetchItems(),
          compartitApi.fetchCompartits()
        ])
        setItems(itemsData)
        setCarpetas(carpetasData)
        setCompartits(compartitsData)
      } catch (error) {
        console.error(error)
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [])

  const filteredItems = items.filter(
    (item) =>
      item.nomUsuari.toLowerCase().includes(search.toLowerCase()) ||
      item.url.toLowerCase().includes(search.toLowerCase())
  )

  const filteredCarpetes = carpetas.filter((carpeta) =>
    carpeta.nom.toLowerCase().includes(search.toLowerCase())
  )

  const filteredCompartits = compartits.filter(
    (compartit) =>
      compartit.item?.titol?.toLowerCase().includes(search.toLowerCase()) ||
      compartit.carpeta?.nom?.toLowerCase().includes(search.toLowerCase())
  )

  const handleFavoriteItemClick = async (item: Item) => {
    const updatedItem = {
      ...item,
      favorit: !item.favorit
    }

    try {
      setItems((prev) =>
        prev.map((i) => (i.uuid === item.uuid ? updatedItem : i))
      )

      await itemsApi.updateItem(updatedItem)
    } catch (error) {
      console.error(error)
    }
  }

  const handleFavoriteCarpetaClick = async (carpeta: Carpeta) => {
    const updatedCarpeta = {
      ...carpeta,
      favorit: !carpeta.favorit
    }

    try {
      setCarpetas((prev) =>
        prev.map((i) => (i.uuid === carpeta.uuid ? updatedCarpeta : i))
      )

      await carpetasApi.updateCarpeta(updatedCarpeta)
    } catch (error) {
      console.error(error)
    }

  }

  return (
    <div className="p-5">
      {loading ? (
        <p>Carregant...</p>
      ) : (
        <>
          <div className="flex gap-3 mb-4">
            <input
              type="text"
              placeholder="Cerca per nom o URL..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full p-2 border border-gray-400 rounded-lg focus:ring-2 focus:ring-purple-200 focus:ring-offset-0 hover:border-gray-600"
            />
            <button className="bg-purple-500 text-white w-40 rounded-lg font-bold text-sm">
              + Add New
            </button>
          </div>

          {filteredItems.length > 0 && (
            <>
              <div className="flex items-center gap-3 mb-3">
                <div className="w-10 h-px bg-gray-300" />

                <span className="text-sm text-gray-500 whitespace-nowrap">
                  Items
                </span>

                <div className="flex-1 h-px bg-gray-300" />
              </div>
              {filteredItems.map((item) => (
                <span
                  key={item.uuid}
                  onClick={() => navigate(`/item/${item.uuid}`)}
                  className="flex items-center gap-3 p-2 border w-full h-16 bg-purple-100 border-purple-300 rounded-lg mb-2 cursor-pointer hover:bg-purple-300 hover:border-purple-400">
                  {imgErrors[item.uuid] ? (
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
                  ) : (
                    <img
                      className="size-6"
                      src={`http://www.google.com/s2/favicons?domain=${item.url}`}
                      alt=""
                      onError={() =>
                        setImgErrors((prev) => ({
                          ...prev,
                          [item.uuid]: true
                        }))
                      }
                    />
                  )}
                  <div className="flex flex-col">
                    <h1 className="text-lg font-bold">{item.titol}</h1>
                    <p>{item.url}</p>
                  </div>
                  <div className="ml-auto flex gap-2">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill={item.favorit ? "currentColor" : "none"}
                      onClick={(e) => {
                        e.stopPropagation()
                        handleFavoriteItemClick(item)
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
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        d="M11.48 3.499a.562.562 0 0 1 1.04 0l2.125 5.111a.563.563 0 0 0 .475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 0 0-.182.557l1.285 5.385a.562.562 0 0 1-.84.61l-4.725-2.885a.562.562 0 0 0-.586 0L6.982 20.54a.562.562 0 0 1-.84-.61l1.285-5.386a.562.562 0 0 0-.182-.557l-4.204-3.602a.562.562 0 0 1 .321-.988l5.518-.442a.563.563 0 0 0 .475-.345L11.48 3.5Z"
                      />
                    </svg>

                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      strokeWidth={1.5}
                      stroke="currentColor"
                      className="size-6 hover:cursor-pointer hover:text-purple-900">
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10"
                      />
                    </svg>

                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke-width="1.5"
                      stroke="currentColor"
                      className="size-6 hover:text-red-600">
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0"
                      />
                    </svg>
                  </div>
                </span>
              ))}
            </>
          )}

          {filteredCarpetes.length > 0 && (
            <>
              <div className="flex items-center gap-3 mb-3">
                <div className="w-10 h-px bg-gray-300" />

                <span className="text-sm text-gray-500 whitespace-nowrap">
                  Carpetes
                </span>

                <div className="flex-1 h-px bg-gray-300" />
              </div>
              {filteredCarpetes.map((carpetes) => (
                <span
                  key={carpetes.uuid}
                  onClick={() => navigate(`/carpeta/${carpetes.uuid}`)}
                  className="flex items-center gap-3 p-2 border w-full h-16 bg-purple-100 border-purple-300 rounded-lg mb-2 cursor-pointer hover:bg-purple-300 hover:border-purple-400">
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
                      d="M2.25 12.75V12A2.25 2.25 0 0 1 4.5 9.75h15A2.25 2.25 0 0 1 21.75 12v.75m-8.69-6.44-2.12-2.12a1.5 1.5 0 0 0-1.061-.44H4.5A2.25 2.25 0 0 0 2.25 6v12a2.25 2.25 0 0 0 2.25 2.25h15A2.25 2.25 0 0 0 21.75 18V9a2.25 2.25 0 0 0-2.25-2.25h-5.379a1.5 1.5 0 0 1-1.06-.44Z"
                    />
                  </svg>

                  <div className="flex flex-col">
                    <h1 className="text-lg font-bold">{carpetes.nom}</h1>
                  </div>
                  <div className="ml-auto flex gap-2">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill={carpetes.favorit ? "currentColor" : "none"}
                      viewBox="0 0 24 24"
                      strokeWidth={1.5}
                      stroke={carpetes.favorit ? "#facc15" : "currentColor"}
                      className={`size-6 hover:cursor-pointer transition-colors ${
                        carpetes.favorit
                          ? "text-yellow-400 hover:text-yellow-600"
                          : "hover:text-yellow-400"
                      }`}>
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        d="M11.48 3.499a.562.562 0 0 1 1.04 0l2.125 5.111a.563.563 0 0 0 .475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 0 0-.182.557l1.285 5.385a.562.562 0 0 1-.84.61l-4.725-2.885a.562.562 0 0 0-.586 0L6.982 20.54a.562.562 0 0 1-.84-.61l1.285-5.386a.562.562 0 0 0-.182-.557l-4.204-3.602a.562.562 0 0 1 .321-.988l5.518-.442a.563.563 0 0 0 .475-.345L11.48 3.5Z"
                      />
                    </svg>

                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      strokeWidth={1.5}
                      stroke="currentColor"
                      className="size-6 hover:cursor-pointer hover:text-purple-900">
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10"
                      />
                    </svg>

                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke-width="1.5"
                      stroke="currentColor"
                      className="size-6 hover:text-red-600">
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0"
                      />
                    </svg>
                  </div>
                </span>
              ))}
            </>
          )}

          {filteredCompartits.length > 0 && (
            <>
              <div className="flex items-center gap-3 mb-3">
                <div className="w-10 h-px bg-gray-300" />

                <span className="text-sm text-gray-500 whitespace-nowrap">
                  Tots els compartits
                </span>

                <div className="flex-1 h-px bg-gray-300" />
              </div>
              {filteredCompartits.map((compartit) => (
                <span
                  key={compartit.uuid}
                  onClick={() => navigate(`/compartit/${compartit.uuid}`)}
                  className="flex items-center gap-3 p-2 border w-full h-16 bg-purple-100 border-purple-300 rounded-lg mb-2 cursor-pointer hover:bg-purple-300 hover:border-purple-400">
                  {compartit.tipusEntitat === TipusEntitat.CARPETA ? (
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
                        d="M2.25 12.75V12A2.25 2.25 0 0 1 4.5 9.75h15A2.25 2.25 0 0 1 21.75 12v.75m-8.69-6.44-2.12-2.12a1.5 1.5 0 0 0-1.061-.44H4.5A2.25 2.25 0 0 0 2.25 6v12a2.25 2.25 0 0 0 2.25 2.25h15A2.25 2.25 0 0 0 21.75 18V9a2.25 2.25 0 0 0-2.25-2.25h-5.379a1.5 1.5 0 0 1-1.06-.44Z"
                      />
                    </svg>
                  ) : imgErrors[compartit.uuid] ? (
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
                  ) : (
                    <img
                      className="size-6"
                      src={`http://www.google.com/s2/favicons?domain=${compartit.item?.url}`}
                      alt=""
                      onError={() =>
                        setImgErrors((prev) => ({
                          ...prev,
                          [compartit.uuid]: true
                        }))
                      }
                    />
                  )}
                  <div className="flex flex-col">
                    <h1 className="text-lg font-bold">
                      {compartit.tipusEntitat === TipusEntitat.CARPETA
                        ? compartit.carpeta?.nom
                        : compartit.item?.titol}
                    </h1>
                    <p>{compartit.item?.url}</p>
                  </div>
                  <div className="ml-auto flex gap-2">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill={
                        compartit.carpeta?.favorit || compartit.item?.favorit
                          ? "currentColor"
                          : "none"
                      }
                      viewBox="0 0 24 24"
                      strokeWidth={1.5}
                      stroke={
                        compartit.carpeta?.favorit || compartit.item?.favorit
                          ? "#facc15"
                          : "currentColor"
                      }
                      className={`size-6 hover:cursor-pointer transition-colors ${
                        compartit.carpeta?.favorit || compartit.item?.favorit
                          ? "text-yellow-400 hover:text-yellow-600"
                          : "hover:text-yellow-400"
                      }`}>
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        d="M11.48 3.499a.562.562 0 0 1 1.04 0l2.125 5.111a.563.563 0 0 0 .475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 0 0-.182.557l1.285 5.385a.562.562 0 0 1-.84.61l-4.725-2.885a.562.562 0 0 0-.586 0L6.982 20.54a.562.562 0 0 1-.84-.61l1.285-5.386a.562.562 0 0 0-.182-.557l-4.204-3.602a.562.562 0 0 1 .321-.988l5.518-.442a.563.563 0 0 0 .475-.345L11.48 3.5Z"
                      />
                    </svg>

                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      strokeWidth={1.5}
                      stroke="currentColor"
                      className="size-6 hover:cursor-pointer hover:text-purple-900">
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10"
                      />
                    </svg>

                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke-width="1.5"
                      stroke="currentColor"
                      className="size-6 hover:text-red-600">
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0"
                      />
                    </svg>
                  </div>
                </span>
              ))}
            </>
          )}
        </>
      )}
    </div>
  )
}

export default Home
