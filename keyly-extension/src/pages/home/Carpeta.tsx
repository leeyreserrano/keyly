import React from "react"
import { useState } from "react"
import CarpetCard from "~components/CarpetCard"
import Searcher from "~components/Searcher"

function Carpeta() {
  const [search, setSearch] = useState("")

  return (
    <div className="p-5">
      <Searcher search={search} setSearch={setSearch} />

      <CarpetCard search={search} />
    </div>
  )
}

export default Carpeta
