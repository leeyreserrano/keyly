import React from "react"
import { useNavigate } from "react-router-dom"

function Navbar() {
  const navigate = useNavigate()

  return (
    <nav className="bg-purple-200 flex items-center justify-center gap-5 h-10 sticky bottom-0 z-50">
      <svg
        className="size-8 cursor-pointer hover:scale-110 transition-transform"
        focusable="false"
        aria-hidden="true"
        viewBox="0 0 24 24"
        onClick={() => navigate('/home')}
        data-testid="HomeRoundedIcon">
        <path d="M10 19v-5h4v5c0 .55.45 1 1 1h3c.55 0 1-.45 1-1v-7h1.7c.46 0 .68-.57.33-.87L12.67 3.6c-.38-.34-.96-.34-1.34 0l-8.36 7.53c-.34.3-.13.87.33.87H5v7c0 .55.45 1 1 1h3c.55 0 1-.45 1-1"></path>
      </svg>

      <svg
        className="size-8 cursor-pointer hover:scale-110 transition-transform"
        focusable="false"
        aria-hidden="true"
        viewBox="0 0 24 24"
        onClick={() => navigate('/item')}
        data-testid="VpnKeyRoundedIcon">
        <path d="M12.65 10C11.7 7.31 8.9 5.5 5.77 6.12c-2.29.46-4.15 2.29-4.63 4.58C.32 14.57 3.26 18 7 18c2.61 0 4.83-1.67 5.65-4H17v2c0 1.1.9 2 2 2s2-.9 2-2v-2c1.1 0 2-.9 2-2s-.9-2-2-2zM7 14c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2"></path>
      </svg>

      <svg
        className="size-8 cursor-pointer hover:scale-110 transition-transform"
        focusable="false"
        aria-hidden="true"
        viewBox="0 0 24 24"
        onClick={() => navigate('/carpeta')}
        data-testid="FolderOutlinedIcon">
        <path d="m9.17 6 2 2H20v10H4V6zM10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8z"></path>
      </svg>

      <svg
        className="size-8 cursor-pointer hover:scale-110 transition-transform"
        focusable="false"
        aria-hidden="true"
        viewBox="0 0 24 24"
        onClick={() => navigate('/compartit')}
        data-testid="PeopleAltOutlinedIcon">
        <path d="M16.67 13.13C18.04 14.06 19 15.32 19 17v3h4v-3c0-2.18-3.57-3.47-6.33-3.87M15 12c2.21 0 4-1.79 4-4s-1.79-4-4-4c-.47 0-.91.1-1.33.24C14.5 5.27 15 6.58 15 8s-.5 2.73-1.33 3.76c.42.14.86.24 1.33.24m-6 0c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4m0-6c1.1 0 2 .9 2 2s-.9 2-2 2-2-.9-2-2 .9-2 2-2m0 7c-2.67 0-8 1.34-8 4v3h16v-3c0-2.66-5.33-4-8-4m6 5H3v-.99C3.2 16.29 6.3 15 9 15s5.8 1.29 6 2z"></path>
      </svg>
    </nav>
  )
}

export default Navbar
