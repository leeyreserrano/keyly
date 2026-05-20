import React from "react"


function ModalConfirmDelete({ open, onClose, onConfirm, item }) {
  if (!open) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div
        className="absolute inset-0 bg-black/40 backdrop-blur-sm"
        onClick={onClose}
      />

      <div className="relative bg-white p-6 rounded-lg shadow-lg z-10">
        <h1 className="text-lg font-semibold mb-4">
          ¿Seguro que quieres eliminar?
        </h1>

        <div className="flex gap-3 justify-end">
          <button
            className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300"
            onClick={onClose}>
            Cancelar
          </button>

          <button
            className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
            onClick={() => onConfirm(item)}>
            Eliminar
          </button>
        </div>
      </div>
    </div>
  )
}

export default ModalConfirmDelete;
