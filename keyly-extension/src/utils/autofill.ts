import type { Item } from "~models/Item"

export async function autofill(item: Item) {
  const [tab] = await chrome.tabs.query({
    active: true,
    currentWindow: true
  })

  if (!tab.id) {
    console.error("❌ No active tab found")
    return
  }

  // Validar que no sea una URL bloqueada
  if (!tab.url || !isUrlAllowed(tab.url)) {
    console.error("❌ Cannot execute script on this URL:", tab.url)
    alert("El autofill no funciona en páginas del sistema de Chrome. Abre un formulario en una página normal.")
    return
  }

  try {
    // Envía mensaje al content script en lugar de executeScript directo
    const response = await chrome.tabs.sendMessage(tab.id, {
      type: "AUTOFILL",
      data: {
        username: item.nomUsuari,
        password: item.contrasenya
      }
    })

    if (response?.success) {
      console.log("✅ Autofill enviado correctamente")
    }
  } catch (error) {
    console.error("❌ Error enviando autofill:", error)
    // Fallback: usar executeScript si el content script no está disponible
    try {
      await chrome.scripting.executeScript({
        target: { tabId: tab.id },
        func: (username: string, password: string) => {
          const usernameField = document.querySelector(
            'input[type="text"], input[type="email"], input[name*="user"], input[name*="email"], input[name*="username"]'
          ) as HTMLInputElement

          const passwordField = document.querySelector(
            'input[type="password"]'
          ) as HTMLInputElement

          if (usernameField) {
            usernameField.value = username
            usernameField.dispatchEvent(new Event("input", { bubbles: true }))
            usernameField.dispatchEvent(new Event("change", { bubbles: true }))
          }

          if (passwordField) {
            passwordField.value = password
            passwordField.dispatchEvent(new Event("input", { bubbles: true }))
            passwordField.dispatchEvent(new Event("change", { bubbles: true }))
          }
        },
        args: [item.nomUsuari, item.contrasenya]
      })

      console.log("✅ Autofill enviado (fallback)")
    } catch (fallbackError) {
      console.error("❌ Fallback también falló:", fallbackError)
    }
  }
}

function isUrlAllowed(url: string): boolean {
  const blockedProtocols = ["chrome://", "about:", "moz-extension://", "edge://", "file://"]
  return !blockedProtocols.some(protocol => url.startsWith(protocol))
}
