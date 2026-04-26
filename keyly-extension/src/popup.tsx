import React from "react"
import { MemoryRouter, Route, Routes } from "react-router-dom"
import Layout from "~components/Layout"
import Home from "~pages/home/Home"
import Item from "~pages/item/Item"
import ItemPage from "~pages/home/Item";
import CarpetaPage from "~pages/home/Carpeta"
import CompartitPage from "~pages/home/Compartit"

import Login from "~pages/login/Login"

import "~style.css"
import NewItem from "~pages/create_item/NewItem"
import NewFolder from "~pages/create_folder/NewFolder"

function IndexPopup() {
  return (
    <div className="w-[500] h-[550]">
      <MemoryRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route element={<Layout />} >
            <Route path="/home" element={<Home />} />
            <Route path="/item" element={<ItemPage />} />
            <Route path="/carpeta" element={<CarpetaPage />} />
            <Route path="/compartit" element={<CompartitPage />} />
            <Route path="/create/item" element={<NewItem />} />
            <Route path="/create/folder" element={<NewFolder />} />
            <Route path="/item/:id" element={<Item />} />
          </Route>
        </Routes>
      </MemoryRouter>
    </div>
  )
}

export default IndexPopup
