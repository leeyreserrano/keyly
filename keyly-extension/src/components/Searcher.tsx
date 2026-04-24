import React, { useState } from "react"

type SearcherProps = {
  search: string
  setSearch: (value: string) => void
}
function Searcher({ search, setSearch }: SearcherProps) {
  return (
    <div className="flex gap-3 mb-4">
      <input
        type="text"
        placeholder="Cerca per nom o URL..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        className="w-full p-2 border border-gray-400 rounded-lg focus:ring-2 focus:ring-purple-200 focus:ring-offset-0 hover:border-gray-600 transition"
      />

      <button className="bg-purple-500 text-white w-40 rounded-lg font-bold text-sm hover:bg-purple-700 transition-colors">
        + Add New
      </button>
    </div>
  )
}
export default Searcher
