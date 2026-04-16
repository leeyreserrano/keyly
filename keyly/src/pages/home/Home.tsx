import { useEffect, useState } from "react"
import { carpetasApi } from "src/api/carpeta-service"
import { itemsApi } from "src/api/item-service"

import type { Carpeta } from "~models/Carpeta"
import type { Item } from "~models/Item"

function Home() {
  const [items, setItems] = useState<Item[]>([])
  const [carpetas, setCarpetas] = useState<Carpeta[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const loadData = async () => {
      try {
        const [itemsData, carpetasData] = await Promise.all([
          itemsApi.fetchItems(),
          carpetasApi.fetchItems()
        ])
        setItems(itemsData)
        setCarpetas(carpetasData)
      } catch (error) {
        console.error(error)
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [])

  return (
    <div className="p-5">
      {loading ? (
        <p>Cargando...</p>
      ) : (
        items.map((item) => (
          <span
            key={item.uuid}
            className="flex items-center gap-3 p-2 border w-full bg-slate-800 rounded-lg mb-2">
            <img
              className="w-8 h-8"
              src={`http://www.google.com/s2/favicons?domain=${item.url}`}
              alt=""
            />

            <div className="flex flex-col">
              <h1 className="text-lg font-bold">{item.nomUsuari}</h1>
              <p>{item.url}</p>
            </div>

            <div className="ml-auto">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                strokeWidth={1.5}
                stroke="currentColor"
                className="w-6 h-6 hover:cursor-pointer hover:text-gray-300">
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10"
                />
              </svg>
            </div>
          </span>
        ))
      )}
    </div>
  )
}

export default Home
