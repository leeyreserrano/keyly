import React, { useState } from "react"
import ItemCard from "~components/ItemCard"
import Searcher from "~components/Searcher"

function Item() {
  const [search, setSearch] = useState("")

  return (
    <div className="p-5">
      <Searcher search={search} setSearch={setSearch} />

      <ItemCard search={search} />
    </div>
  )
}

export default Item
