import React, { useState } from "react"

import CarpetCard from "~components/CarpetCard"
import CompartitCard from "~components/CompartitCard"
import ItemCard from "~components/ItemCard"
import Searcher from "~components/Searcher"

function Home() {
  const [search, setSearch] = useState("")
  const [showMenu, setShowMenu] = useState(false)

  return (
    <div className="p-5">
      <Searcher
        search={search}
        setSearch={setSearch}
        onAddClick={() => setShowMenu(!showMenu)}
      />

      {showMenu && (
        <div className="absolute right-5 -mt-4 bg-white border rounded-lg shadow p-2 z-10 text-sm">
          <div className="flex items-center justify-center w-30 hover:bg-gray-100 cursor-pointer rounded-lg">
            <svg fill="currentColor" className="size-6" viewBox="0 0 24 24">
              <path d="M12.65 10C11.7 7.31 8.9 5.5 5.77 6.12c-2.29.46-4.15 2.29-4.63 4.58C.32 14.57 3.26 18 7 18c2.61 0 4.83-1.67 5.65-4H17v2c0 1.1.9 2 2 2s2-.9 2-2v-2c1.1 0 2-.9 2-2s-.9-2-2-2zM7 14c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2"></path>
            </svg>
            <button className="block w-full text-left p-2">
              Item
            </button>
          </div>

          <hr />

          <div className="flex items-center justify-center hover:bg-gray-100 cursor-pointer rounded-lg">
            <svg fill="currentColor" className="size-6" viewBox="0 0 24 24">
              <path d="m9.17 6 2 2H20v10H4V6zM10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8z"></path>
            </svg>
            <button className="block w-full text-left p-2">
              Carpeta
            </button>
          </div>
        </div>
      )}

      <ItemCard search={search} />
      <CarpetCard search={search} />
      <CompartitCard search={search} />
    </div>
  )
}

export default Home
