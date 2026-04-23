import React from "react"
import { MemoryRouter, Route, Routes } from "react-router-dom"
import Layout from "~components/Layout"
import Home from "~pages/home/Home"
import Item from "~pages/item/Item"

import Login from "~pages/login/Login"

import "~style.css"

function IndexPopup() {
  return (
    <div className="w-[500] h-[550]">
      <MemoryRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route element={<Layout />} >
            <Route path="/home" element={<Home />} />
            <Route path="/item/:id" element={<Item />} />
          </Route>
        </Routes>
      </MemoryRouter>
    </div>
  )
}

export default IndexPopup
