import React, { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"

import { carpetasApi } from "~api/carpeta-service"
import type { Carpeta } from "~models/Carpeta"

import ModalConfirmDelete from "./ModalConfimDelete"

function CarpetCard({ search }: { search: string }) {
  const [carpetas, setCarpetas] = useState<Carpeta[]>([])
  const navigate = useNavigate()
  const [showMenu, setShowMenu] = useState(false)
  const [selectedCarpeta, setSelectedCarpeta] = useState(null)

  useEffect(() => {
    const loadCarpeta = async () => {
      try {
        const [carpetasData] = await Promise.all([carpetasApi.fetchCarpetas()])
        setCarpetas(carpetasData)
      } catch (error) {
        console.log(error)
      }
    }

    loadCarpeta()
  }, [])

  const filteredCarpetes = carpetas.filter((carpeta) =>
    carpeta.nom.toLowerCase().includes(search.toLowerCase())
  )

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

  const handleDeleteCarpetaClick = async (carpeta: Carpeta) => {
    try {
      await carpetasApi.deleteCarpeta(carpeta.uuid)

      setCarpetas((prev) => prev.filter((i) => i.uuid !== carpeta.uuid))
      setShowMenu(false)
    } catch (error) {
      console.error(error)
    }
  }

  return (
    <>
      <div className="mb-3">
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
                onClick={() =>
                  navigate(`/carpeta/${carpetes.uuid}`, { state: carpetes })
                }
                className="flex items-center gap-3 p-2 border w-full h-16 bg-purple-100 border-purple-300 rounded-lg mb-2 cursor-pointer hover:bg-purple-300 hover:border-purple-400 transition-colors">
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

                <div className="flex flex-col min-w-0">
                  <h1 className="text-lg font-bold truncate">{carpetes.nom}</h1>
                </div>
                <div className="ml-auto flex gap-2">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill={carpetes.favorit ? "currentColor" : "none"}
                    viewBox="0 0 24 24"
                    strokeWidth={1.5}
                    onClick={(e) => {
                      e.stopPropagation()
                      handleFavoriteCarpetaClick(carpetes)
                    }}
                    stroke={carpetes.favorit ? "#facc15" : "currentColor"}
                    className={`size-6 hover:curscor-pointer transition-colors ${
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
                    stroke-width="1.5"
                    stroke="currentColor"
                    onClick={(e) => {
                      e.stopPropagation()
                      setSelectedCarpeta(carpetes)
                      setShowMenu(true)
                    }}
                    className="size-6 hover:text-red-600 transition-colors">
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
      </div>
      <ModalConfirmDelete
        open={showMenu}
        item={selectedCarpeta}
        onClose={() => setShowMenu(false)}
        onConfirm={(carpeta) => {
          handleDeleteCarpetaClick(carpeta)
        }}
      />
    </>
  )
}

export default CarpetCard
