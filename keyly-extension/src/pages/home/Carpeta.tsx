import React, { useState } from "react"
import { useLocation, useNavigate } from "react-router-dom"

import CarpetCard from "~components/CarpetCard"
import Searcher from "~components/Searcher"

function Carpeta() {
  const [search, setSearch] = useState("")
  const navigate = useNavigate()
  const location = useLocation()

  return (
    <div className="p-5">
      <Searcher
        search={search}
        setSearch={setSearch}
        onAddClick={() =>
          navigate("/create/folder", {
            state: { from: location.pathname }
          })
        }
      />

      <CarpetCard search={search} />
    </div>
  )
}

export default Carpeta
