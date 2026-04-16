import { useState } from "react"
import { useNavigate } from "react-router-dom"
import AuthService from "src/api/auth-service"

function Login() {
  const [correu, setCorreu] = useState("")
  const [contrasenya, setContrasenya] = useState("")
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    try {
      const result = await AuthService(correu, contrasenya, true)

      navigate("/home")
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <form
      className="flex flex-col items-center justify-center gap-4 mt-5 mx-5"
      onSubmit={handleSubmit}>
      <input
        className="border rounded-lg p-2 w-full text-sm hover:border-gray-400"
        placeholder="Email"
        type="email"
        value={correu}
        onChange={(e) => setCorreu(e.target.value)}
      />

      <div className="relative w-full">
        <input
          className="border p-2 rounded-lg w-full text-sm hover:border-gray-400 pr-10"
          placeholder="Contrasenya"
          name="contrasenya"
          id="contrasenya"
          type={showPassword ? "text" : "password"}
          value={contrasenya}
          onChange={(e) => setContrasenya(e.target.value)}
        />

        <button
          type="button"
          onClick={() => setShowPassword(!showPassword)}
          className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-600 hover:text-gray-900">
          {showPassword ? (
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              strokeWidth={1.5}
              stroke="currentColor"
              className="w-5 h-5">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M3.98 8.223A10.477 10.477 0 0 0 1.934 12C3.226 16.338 7.244 19.5 12 19.5c.993 0 1.953-.138 2.863-.395M6.228 6.228A10.451 10.451 0 0 1 12 4.5c4.756 0 8.773 3.162 10.065 7.498a10.522 10.522 0 0 1-4.293 5.774M6.228 6.228 3 3m3.228 3.228 3.65 3.65m7.894 7.894L21 21m-3.228-3.228-3.65-3.65m0 0a3 3 0 1 0-4.243-4.243m4.242 4.242L9.88 9.88"
              />
            </svg>
          ) : (
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              strokeWidth={1.5}
              stroke="currentColor"
              className="w-5 h-5">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M2.036 12.322a1.012 1.012 0 0 1 0-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178Z"
              />
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"
              />
            </svg>
          )}
        </button>
      </div>

      <button
        className="bg-blue-500 text-white px-4 py-2 rounded-lg w-full text-lg hover:bg-blue-600 transition-colors duration-300"
        type="submit">
        Login
      </button>
    </form>
  )
}

export default Login
