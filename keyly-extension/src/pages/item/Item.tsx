import React, { useEffect, useState } from "react"
import { useLocation } from "react-router-dom"

import { itemsApi } from "~api/item-service"

function Item() {
  const [showPassword, setShowPassword] = useState(false)
  const { state: item } = useLocation()

  const getFavicon = (url) => {
    try {
      const domain = new URL(url).hostname
      return `https://icons.duckduckgo.com/ip3/${domain}.ico`
    } catch {
      return null
    }
  }

  useEffect(() => {
    itemsApi.accesItem(item.uuid)
  })
  return (
    <div className="relative flex flex-col flex-1 h-full w-full overflow-hidden bg-gray-50">
      <div className="flex flex-col items-center flex-1 overflow-y-auto py-8">
        <div className="w-[400px] bg-white rounded-2xl shadow-md border border-gray-200 p-6 space-y-5 transition hover:shadow-lg">
          {/* HEADER CON LOGO */}
          <div className="relative flex items-center justify-center border-b pb-4">
            {/* LOGO IZQUIERDA */}
            <div className="absolute left-0">
              {item.url && getFavicon(item.url) ? (
                <a
                  href={item.url}
                  target="_blank"
                  className="hover:cursor-pointer">
                  <img
                    src={getFavicon(item.url)}
                    alt="logo"
                    className="w-12 h-12 rounded-lg border p-1 bg-white"
                    onError={(e) => {
                      e.currentTarget.style.display = "none"
                    }}
                  />
                </a>
              ) : (
                <div className="w-12 h-12 flex items-center justify-center rounded-lg bg-gray-200">
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
                </div>
              )}
            </div>

            {/* TÍTULO CENTRADO REAL */}
            <h1 className="text-2xl font-semibold text-gray-800 text-center">
              {item.titol}
            </h1>
          </div>

          {/* PASSWORD */}
          <div>
            <p className="text-xs text-gray-500 mb-1">Contrasenya</p>

            <div className="flex items-center justify-between bg-gray-50 border rounded-lg px-3 py-2">
              <p className="text-gray-800 break-all">
                {showPassword ? item.contrasenya : "••••••••••"}
              </p>

              <div className="flex items-center gap-2">
                {/* SHOW / HIDE */}
                <button
                  onClick={() => setShowPassword(!showPassword)}
                  className="text-gray-500 hover:text-gray-800">
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

                {/* COPY */}
                <button
                  onClick={() =>
                    navigator.clipboard.writeText(item.contrasenya)
                  }
                  className="text-gray-500 hover:text-gray-800">
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
          </div>

          {/* URL */}
          <div>
            <p className="text-xs text-gray-500">URL</p>
            <a
              href={item.url}
              target="_blank"
              rel="url"
              className="text-purple-500 break-all hover:underline">
              {item.url || "-"}
            </a>
          </div>

          {/* NOTES */}
          <div>
            <p className="text-xs text-gray-500">Notes</p>
            <p className="text-gray-700">{item.notes || "-"}</p>
          </div>

          {/* FAVORIT */}
          <div>
            <p className="text-xs text-gray-500">Favorit</p>
            <p className="text-gray-800">{item.favorit ? "⭐ Sí" : "No"}</p>
          </div>

          {/* META INFO */}
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <p className="text-xs text-gray-500">Creat</p>
              <p className="text-gray-700">
                {item.dataCreacio
                  ? new Date(item.dataCreacio).toLocaleString("es-ES")
                  : "-"}
              </p>
            </div>

            <div>
              <p className="text-xs text-gray-500">Editat</p>
              <p className="text-gray-700">
                {item.dataEditat
                  ? new Date(item.dataEditat).toLocaleDateString("es-ES")
                  : "-"}
              </p>
            </div>

            <div>
              <p className="text-xs text-gray-500">Últim accés</p>
              <p className="text-gray-700">
                {item.ultimAcces
                  ? new Date(item.ultimAccess).toLocaleDateString("es-ES")
                  : "-"}
              </p>
            </div>

            <div>
              <p className="text-xs text-gray-500">Carpeta</p>
              <p className="text-gray-700">
                {item.dinsDeCarpeta ? "Sí" : "No"}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Item
