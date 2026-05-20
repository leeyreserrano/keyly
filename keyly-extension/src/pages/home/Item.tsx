import React, { useState } from "react"
import { useLocation, useNavigate } from "react-router-dom"

import ItemCard from "~components/ItemCard"
import Searcher from "~components/Searcher"

function Item() {
  const [search, setSearch] = useState("")
  const navigate = useNavigate()
  const location = useLocation()

  return (
    <div className="p-5">
      <Searcher
        search={search}
        setSearch={setSearch}
        onAddClick={() =>
          navigate("/create/item", {
            state: { from: location.pathname }
          })
        }
      />

      <ItemCard search={search} />
    </div>
  )
}

export default Item
