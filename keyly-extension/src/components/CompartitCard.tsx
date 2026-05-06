import React, { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"

import { compartitApi } from "~api/compartit-service"
import { Permisos, TipusEntitat, type Compartit } from "~models/Compartit"

import { decryptItem } from "./ItemCard"
import ModalConfirmDelete from "./ModalConfimDelete"

function CompartitCard({ search }: { search: string }) {
  const [compartits, setCompartits] = useState<Compartit[]>([])
  const navigate = useNavigate()
  const [imgErrors, setImgErrors] = useState({})
  const [showMenu, setShowMenu] = useState(false)
  const [selectedCompartit, setSelectedCompartit] = useState(null)
  const [modalCompartit, setModalCompartit] = useState<Compartit | null>(null)

  useEffect(() => {
    const loadCompartit = async () => {
      try {
        const compartitsData = await compartitApi.fetchCompartits()

        const decrypted = await Promise.all(
          compartitsData.map(async (compartit) => {
            if (
              compartit.tipusEntitat === TipusEntitat.ITEM &&
              compartit.item
            ) {
              return {
                ...compartit,
                item: await decryptItem(compartit.item)
              }
            }
            if (
              compartit.tipusEntitat === TipusEntitat.CARPETA &&
              compartit.carpeta?.items
            ) {
              const decryptedItems = await Promise.all(
                compartit.carpeta.items.map(decryptItem)
              )
              return {
                ...compartit,
                carpeta: { ...compartit.carpeta, items: decryptedItems }
              }
            }
            return compartit
          })
        )
        console.log(decrypted)
        setCompartits(decrypted)
      } catch (error) {
        console.error(error)
      }
    }
    loadCompartit()
  }, [])

  const filteredCompartits = compartits.filter(
    (compartit) =>
      compartit.item?.titol?.toLowerCase().includes(search.toLowerCase()) ||
      compartit.carpeta?.nom?.toLowerCase().includes(search.toLowerCase())
  )

  const handleFavoriteCompartitClick = async (compartit: Compartit) => {
    const updatedCompartit = {
      ...compartit,
      item: compartit.item
        ? { ...compartit.item, favorit: !compartit.item.favorit }
        : null,
      carpeta: compartit.carpeta
        ? { ...compartit.carpeta, favorit: !compartit.carpeta.favorit }
        : null
    }

    try {
      setCompartits((prev) =>
        prev.map((i) => (i.uuid === compartit.uuid ? updatedCompartit : i))
      )

      await compartitApi.updateCompartit(updatedCompartit)
    } catch (error) {
      console.error(error)
    }
  }

  const handleDeleteCompartitClick = async (compartit: Compartit) => {
    try {
      await compartitApi.deleteCompartit(compartit.uuid)

      setCompartits((prev) => prev.filter((i) => i.uuid !== compartit.uuid))
      setShowMenu(false)
    } catch (error) {
      console.error(error)
    }
  }

  return (
    <>
      <div className="mb-3">
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
                onClick={() =>
                  navigate(
                    compartit.tipusEntitat === TipusEntitat.CARPETA
                      ? `/carpeta/${compartit.uuid}`
                      : `/item/${compartit.uuid}`,
                    { state: compartit.item }
                  )
                }
                className="flex items-center gap-3 p-2 border w-full h-16 bg-purple-100 border-purple-300 rounded-lg mb-2 cursor-pointer hover:bg-purple-300 hover:border-purple-400 transition-colors">
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
                <div className="flex flex-col min-w-0">
                  <h1 className="text-lg font-bold truncate">
                    {compartit.tipusEntitat === TipusEntitat.CARPETA
                      ? compartit.carpeta?.nom
                      : compartit.item?.titol}
                  </h1>
                  <p className="truncate text-sm text-gray-600">
                    {compartit.item?.url}
                  </p>
                </div>
                <div className="ml-auto flex gap-2">
                  {/* Compartit */}
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth="1.5"
                    stroke="currentColor"
                    className="size-6 hover:text-gray-600"
                    onClick={(e) => {
                      e.stopPropagation()
                      setModalCompartit(compartit)
                    }}>
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      d="M7.217 10.907a2.25 2.25 0 1 0 0 2.186m0-2.186c.18.324.283.696.283 1.093s-.103.77-.283 1.093m0-2.186 9.566-5.314m-9.566 7.5 9.566 5.314m0 0a2.25 2.25 0 1 0 3.935 2.186 2.25 2.25 0 0 0-3.935-2.186Zm0-12.814a2.25 2.25 0 1 0 3.933-2.185 2.25 2.25 0 0 0-3.933 2.185Z"
                    />
                  </svg>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill={
                      compartit.carpeta?.favorit || compartit.item?.favorit
                        ? "currentColor"
                        : "none"
                    }
                    viewBox="0 0 24 24"
                    strokeWidth={1.5}
                    onClick={(e) => {
                      e.stopPropagation()
                      handleFavoriteCompartitClick(compartit)
                    }}
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
                  {compartit.permisos !== Permisos.LECTURA && (
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      strokeWidth={1.5}
                      stroke="currentColor"
                      className="size-6 hover:cursor-pointer hover:text-purple-900 transition-colors">
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10"
                      />
                    </svg>
                  )}

                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke-width="1.5"
                    stroke="currentColor"
                    onClick={(e) => {
                      e.stopPropagation()
                      setSelectedCompartit(compartit)
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
        item={selectedCompartit}
        onClose={() => setShowMenu(false)}
        onConfirm={(compartit) => {
          handleDeleteCompartitClick(compartit)
        }}
      />
      {modalCompartit &&
        (() => {
          const entitatUuid =
            modalCompartit.tipusEntitat === TipusEntitat.CARPETA
              ? modalCompartit.carpeta?.uuid
              : modalCompartit.item?.uuid

          const totsElsReceptors = compartits.filter(
            (c) =>
              c.tipusEntitat === modalCompartit.tipusEntitat &&
              (c.tipusEntitat === TipusEntitat.CARPETA
                ? c.carpeta?.uuid === entitatUuid
                : c.item?.uuid === entitatUuid)
          )

          const nom =
            modalCompartit.tipusEntitat === TipusEntitat.CARPETA
              ? modalCompartit.carpeta?.nom
              : modalCompartit.item?.titol

          const permisLabel = (p: string) => {
            if (p === "LECTURA") return "Lectura"
            if (p === "ESCRIPTURA") return "Escriptura"
            if (p === "ADMINISTRADOR") return "Administrador"
            return p
          }
          return (
            <div
              className="fixed inset-0 bg-black/40 z-50 flex items-center justify-center"
              onClick={() => setModalCompartit(null)}>
              <div
                className="bg-white rounded-2xl shadow-xl w-[380px] p-6 space-y-4"
                onClick={(e) => e.stopPropagation()}>
                {/* Títol */}
                <div className="flex items-center justify-between border-b pb-3">
                  <div>
                    <p className="text-xs text-gray-400 uppercase tracking-wide">
                      {modalCompartit.tipusEntitat === TipusEntitat.CARPETA
                        ? "Carpeta"
                        : "Item"}
                    </p>
                    <h2 className="text-lg font-bold">{nom}</h2>
                  </div>
                  <button onClick={() => setModalCompartit(null)}>
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

                {/* El teu rol */}
                <div>
                  <p className="text-xs text-gray-500 mb-1">El teu permís</p>
                  <span className="inline-block bg-purple-100 text-purple-700 text-xs font-medium px-3 py-1 rounded-full">
                    {permisLabel(modalCompartit.permisos)}
                  </span>
                </div>

                {/* Creador */}
                <div>
                  <p className="text-xs text-gray-500 mb-1">Compartit per</p>
                  <div className="flex items-center gap-2 border rounded-lg px-3 py-2">
                    <div className="size-7 rounded-full bg-purple-200 flex items-center justify-center text-xs font-bold text-purple-700">
                      {modalCompartit.usuariCreador.nom.charAt(0).toUpperCase()}
                    </div>
                    <div>
                      <p className="text-sm font-medium">
                        {modalCompartit.usuariCreador.nom}
                      </p>
                      <p className="text-xs text-gray-400">
                        {modalCompartit.usuariCreador.correu}
                      </p>
                    </div>
                  </div>
                </div>

                {/* Tots els receptors */}
                <div>
                  <p className="text-xs text-gray-500 mb-1">Compartit amb</p>
                  <div className="space-y-1 max-h-48 overflow-y-auto">
                    {totsElsReceptors.map((c) => (
                      <div
                        key={c.uuid}
                        className="flex items-center justify-between border rounded-lg px-3 py-2">
                        <div className="flex items-center gap-2">
                          <div className="size-7 rounded-full bg-gray-100 flex items-center justify-center text-xs font-bold text-gray-500">
                            {c.usuariReceptor.nom.charAt(0).toUpperCase()}
                          </div>
                          <div>
                            <p className="text-sm font-medium">
                              {c.usuariReceptor.nom}
                            </p>
                            <p className="text-xs text-gray-400">
                              {c.usuariReceptor.correu}
                            </p>
                          </div>
                        </div>
                        <span className="text-xs text-purple-600 font-medium">
                          {permisLabel(c.permisos)}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          )
        })()}
    </>
  )
}

export default CompartitCard
