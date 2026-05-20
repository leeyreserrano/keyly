import type { PlasmoCSConfig } from "plasmo"
export const config: PlasmoCSConfig = {
  matches: ["<all_urls>"],
  run_at: "document_idle"
}

function fillInputs({ username, password }) {
  const inputs = document.querySelectorAll("input")

  const userInput = [...inputs].find(
    (i: HTMLInputElement) =>
      i.type === "text" ||
      i.type === "email" ||
      i.name?.toLowerCase().includes("user") ||
      i.name?.toLowerCase().includes("email")
  ) as HTMLInputElement

  const passInput = [...inputs].find(
    (i: HTMLInputElement) => i.type === "password"
  ) as HTMLInputElement

  if (userInput) {
    userInput.value = username
    userInput.dispatchEvent(new Event("input", { bubbles: true }))
  }

  if (passInput) {
    passInput.value = password
    passInput.dispatchEvent(new Event("input", { bubbles: true }))
  }
}

chrome.runtime.onMessage.addListener((msg) => {
  if (msg.type === "AUTOFILL") {
    fillInputs(msg.data)
  }
})
