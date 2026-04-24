import React, { useState } from "react"

import CompartitCard from "~components/CompartitCard"
import Searcher from "~components/Searcher"

function Compartit() {
  const [search, setSearch] = useState("")

  return (
    <div className="p-5">
      <Searcher search={search} setSearch={setSearch} />

      <CompartitCard search={search} />
    </div>
  )
}

export default Compartit
