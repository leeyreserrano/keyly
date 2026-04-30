import React, { useEffect, useState } from "react"
import { useLocation, useNavigate } from "react-router-dom"

import { carpetasApi } from "~api/carpeta-service"
import { itemsApi } from "~api/item-service"
import type { Carpeta } from "~models/Carpeta"
import type { Item } from "~models/Item"

function NewFolder() {
  const [nomCarpeta, setNomCarpeta] = useState("")
  const navigate = useNavigate()
  const location = useLocation()
  const [items, setItems] = useState<Item[]>([])
  const [selectItems, setSelectItems] = useState([])

  const from = location.state?.from || "/home"

  const handleSubmit = async (e) => {
    e.preventDefault()
    const carpeta: Carpeta = await carpetasApi.createCarpeta(nomCarpeta)
    if (selectItems.length > 0) {
      for (const item of selectItems) {
        await carpetasApi.addItemInCarpeta(carpeta.uuid, item)
      }
    }

    navigate(from)
  }

  useEffect(() => {
    const loadItems = async () => {
      const [itemsData] = await Promise.all([itemsApi.fetchItems()])
      setItems(itemsData)
    }
    loadItems()
  }, [])

  return (
    <div className="relative flex flex-col flex-1 h-full w-full overflow-hidden">
      <form
        className="flex flex-col items-center gap-5 flex-1 overflow-y-auto pb-10 pt-5"
        id="newCarpeta"
        action=""
        onSubmit={handleSubmit}>
        <div className="relative w-80">
          <input
            type="text"
            value={nomCarpeta}
            onChange={(e) => setNomCarpeta(e.target.value)}
            required
            className="w-full border border-gray-400 rounded-lg p-2 pt-4 focus:outline-none focus:ring-2 focus:ring-purple-200"
          />
          <label className="absolute left-3 -top-2 bg-white px-1 text-sm text-gray-600">
            Nom carpeta *
          </label>
        </div>
        <div className="relative w-80">
          <label className="absolute left-3 -top-2 bg-white px-1 text-sm text-gray-600">
            Afegir items
          </label>

          <select
            multiple
            value={selectItems}
            onChange={(e) => {
              const values = Array.from(
                e.target.selectedOptions,
                (option) => option.value
              )
              setSelectItems(values)
            }}
            className="w-full border border-gray-400 rounded-lg p-2 pt-4">
            {" "}
            {items.map((item) => (
              <option key={item.uuid} value={item.uuid}>
                {item.titol}
              </option>
            ))}
          </select>
        </div>
      </form>
      <div className="absolute bottom-3 right-12 z-50">
        <button
          onClick={() => navigate(from)}
          className="bg-purple-400 text-sm text-white border-purple-600 border px-4 py-2 rounded-lg shadow-md hover:bg-purple-500">
          Cancelar
        </button>
      </div>
      <div className="absolute bottom-3 right-36 z-50">
        <button
          type="submit"
          form="newCarpeta"
          className="bg-purple-400 text-sm text-white border-purple-600 border px-4 py-2 rounded-lg shadow-md hover:bg-purple-500">
          Guardar
        </button>
      </div>
    </div>
  )
}

export default NewFolder
