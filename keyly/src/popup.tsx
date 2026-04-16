import { MemoryRouter, Route, Routes } from "react-router-dom"
import Home from "~pages/home/Home"

import Login from "~pages/login/Login"

import "~style.css"

function IndexPopup() {
  return (
    <div className="w-[500] h-[550]">
      <MemoryRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/home" element={<Home />} />
        </Routes>
      </MemoryRouter>
    </div>
  )
}

export default IndexPopup
