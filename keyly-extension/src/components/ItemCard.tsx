import React, { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"

import { itemsApi } from "~api/item-service"
import type { Item } from "~models/Item"

import ModalConfirmDelete from "./ModalConfimDelete"

function ItemCard({ search }: { search: string }) {
  const [items, setItems] = useState<Item[]>([])
  const navigate = useNavigate()
  const [imgErrors, setImgErrors] = useState({})
  const [showMenu, setShowMenu] = useState(false)
  const [selectedItem, setSelectedItem] = useState(null)

  useEffect(() => {
    const loadItems = async () => {
      try {
        const [itemsData] = await Promise.all([itemsApi.fetchItems()])

        const decryptedItems = await Promise.all(itemsData.map(decryptItem))

        console.log("Items desencriptados:", decryptedItems)

        setItems(decryptedItems)
      } catch (error) {
        console.error(error)
      }
    }
    loadItems()
  }, [])
  const filteredItems = items.filter(
    (item) =>
      item.titol.toLowerCase().includes(search.toLowerCase()) ||
      item.url.toLowerCase().includes(search.toLowerCase())
  )

  const getFavicon = (url) => {
    try {
      const domain = new URL(url).hostname
      return `https://icons.duckduckgo.com/ip3/${domain}.ico`
    } catch {
      return null
    }
  }

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

  const handleDeleteItemClick = async (item: Item) => {
    try {
      await itemsApi.deleteItem(item.uuid)

      setItems((prev) => prev.filter((i) => i.uuid !== item.uuid))
      setShowMenu(false)
    } catch (error) {
      console.error(error)
    }
  }

  return (
    <>
      <div className="mb-3">
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
                onClick={() => navigate(`/item/${item.uuid}`, { state: item })}
                className="flex items-center gap-3 p-2 border w-full h-16 bg-purple-100 border-purple-300 rounded-lg mb-2 cursor-pointer hover:bg-purple-300 hover:border-purple-400 transition-colors">
                {getFavicon(item.url) ? (
                  <img
                    className="size-6"
                    src={getFavicon(item.url)}
                    alt=""
                    onError={() =>
                      setImgErrors((prev) => ({
                        ...prev,
                        [item.uuid]: true
                      }))
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
                  {item.dinsDeCarpeta && (
                    <svg
                      fill="currentColor"
                      className="size-6"
                      viewBox="0 0 24 24">
                      <path d="m9.17 6 2 2H20v10H4V6zM10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8z"></path>
                    </svg>
                  )}

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
                    className="size-6 hover:cursor-pointer hover:text-purple-900 transition-colors"
                    onClick={(e) => {
                        e.stopPropagation()
                        navigate(`/item/edit/${item.uuid}`, { state: item })
                    }}>
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
                    onClick={(e) => {
                      e.stopPropagation()
                      setSelectedItem(item)
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
        item={selectedItem}
        onClose={() => setShowMenu(false)}
        onConfirm={(item) => {
          handleDeleteItemClick(item)
        }}
      />
    </>
  )
}

export default ItemCard

export async function decryptItem(item: any): Promise<Item> {
  try {
    const privateKeyB64 = localStorage.getItem("privateKey")
    if (!privateKeyB64) throw new Error("No hay private key en localStorage")

    const privateKeyBytes = Uint8Array.from(atob(privateKeyB64), (c) =>
      c.charCodeAt(0)
    )
    const privateKey = await crypto.subtle.importKey(
      "pkcs8",
      privateKeyBytes,
      { name: "RSA-OAEP", hash: "SHA-256" },
      false,
      ["decrypt"]
    )

    const encryptedDataKeyBytes = Uint8Array.from(
      atob(item.encryptedDataKey.encryptedDatakey),
      (c) => c.charCodeAt(0)
    )
    const dataKeyBuffer = await crypto.subtle.decrypt(
      { name: "RSA-OAEP" },
      privateKey,
      encryptedDataKeyBytes
    )

    const dataKey = await crypto.subtle.importKey(
      "raw",
      dataKeyBuffer,
      { name: "AES-GCM" },
      false,
      ["decrypt"]
    )

    const iv = Uint8Array.from(atob(item.iv), (c) => c.charCodeAt(0))
    const encryptedPswBytes = Uint8Array.from(atob(item.contrasenya), (c) =>
      c.charCodeAt(0)
    )
    const decryptedPswBuffer = await crypto.subtle.decrypt(
      { name: "AES-GCM", iv },
      dataKey,
      encryptedPswBytes
    )

    const contrasenya = new TextDecoder().decode(decryptedPswBuffer)

    return {
      ...item,
      contrasenya,
      encryptedDataKey: item.encryptedDataKey.uuid
    }
  } catch (err) {
    console.error("Error desencriptando item:", item.uuid, err)
    return {
      ...item,
      contrasenya: "",
      encryptedDataKey: item.encryptedDataKey?.uuid
    }
  }
}

export async function decryptItemWithRawKey(item: any): Promise<{ item: Item, rawDataKey: ArrayBuffer }> {
  const privateKeyB64 = localStorage.getItem("privateKey")
  if (!privateKeyB64) throw new Error("No hay private key en localStorage")

  const privateKeyBytes = Uint8Array.from(atob(privateKeyB64), (c) => c.charCodeAt(0))
  const privateKey = await crypto.subtle.importKey(
    "pkcs8",
    privateKeyBytes,
    { name: "RSA-OAEP", hash: "SHA-256" },
    false,
    ["decrypt"]
  )

  const encryptedDataKeyBytes = Uint8Array.from(
    atob(item.encryptedDataKey.encryptedDatakey),
    (c) => c.charCodeAt(0)
  )

  const rawDataKey = await crypto.subtle.decrypt(
    { name: "RSA-OAEP" },
    privateKey,
    encryptedDataKeyBytes
  )

  const dataKey = await crypto.subtle.importKey(
    "raw",
    rawDataKey,
    { name: "AES-GCM" },
    false,
    ["decrypt"]
  )

  const iv = Uint8Array.from(atob(item.iv), (c) => c.charCodeAt(0))
  const encryptedPswBytes = Uint8Array.from(atob(item.contrasenya), (c) => c.charCodeAt(0))
  const decryptedPswBuffer = await crypto.subtle.decrypt(
    { name: "AES-GCM", iv },
    dataKey,
    encryptedPswBytes
  )

  const contrasenya = new TextDecoder().decode(decryptedPswBuffer)

  return {
    rawDataKey,
    item: {
      ...item,
      contrasenya,
      encryptedDataKey: item.encryptedDataKey.uuid
    }
  }
}
