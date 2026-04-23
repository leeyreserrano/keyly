import { Outlet } from "react-router-dom"
import Navbar from "./Navbar"
import Header from "./Header"

function Layout() {
  return (
    <div className="flex flex-col min-h-screen">
        <Header />
      <main className="flex-1">
        <Outlet />
      </main>

      <Navbar />
    </div>
  )
}

export default Layout