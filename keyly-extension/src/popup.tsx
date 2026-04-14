import { MemoryRouter, Route, Routes } from "~node_modules/react-router-dom/dist"
import Login from "~src/pages/login/Login"

function IndexPopup() {
  return (
    <MemoryRouter>
      <Routes>
        <Route path="/" element={<Login />} />
      </Routes>
    </MemoryRouter>
  )
}

export default IndexPopup
