import { Outlet } from "react-router-dom"
import Navbar from "./Navbar"
import Header from "./Header"
import React from "react"

function Layout() {
  return (
    <div className="flex flex-col h-screen w-full overflow-hidden">
        <Header />
      <main className="flex-1 overflow-y-auto relative flex flex-col">
        <Outlet />
      </main>

      <Navbar />
    </div>
  )
}

export default Layout