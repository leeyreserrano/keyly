import { BrowserRouter, Routes, Route, Navigate } from 'react-router';
import { Toaster } from 'react-hot-toast';
import { AuthProvider} from './context/AuthContext';
import ProtectedRoute from './routes/ProtectedRoute';
import Login from './pages/Login';
import Home from './pages/Home';
import Item from './pages/Items/Item';
import AddItem from './pages/Items/AddItem';
import ChooseType from './pages/ChooseType';
import Items from './pages/Items/Items';
import EditItem from './pages/Items/EditItem';
import Carpetas from './pages/Carpetes/Carpetes';
import Carpeta from './pages/Carpetes/Carpeta';
import AddCarpeta from './pages/Carpetes/AddFolder';
import UserConfig from './pages/UserConfig';
import EditCarpeta from './pages/Carpetes/EditFolder';
import Compartits from './pages/Compartit/Compartits';

import './App.css';

function App() {
  return (
    <AuthProvider>
      <Toaster position="top-center" />
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/home"      element={<ProtectedRoute><Home /></ProtectedRoute>} />
          <Route path="/Item"      element={<ProtectedRoute><Item /></ProtectedRoute>} />
          <Route path="/AddItem"   element={<ProtectedRoute><AddItem /></ProtectedRoute>} />
          <Route path="/ChooseType" element={<ProtectedRoute><ChooseType /></ProtectedRoute>} />
          <Route path="/Items"     element={<ProtectedRoute><Items /></ProtectedRoute>} />
          <Route path="/EditItem"  element={<ProtectedRoute><EditItem /></ProtectedRoute>} />
          <Route path="/Carpetes"  element={<ProtectedRoute><Carpetas /></ProtectedRoute>} />
          <Route path="/Carpeta"   element={<ProtectedRoute><Carpeta /></ProtectedRoute>} />
          <Route path="/AddCarpeta" element={<ProtectedRoute><AddCarpeta /></ProtectedRoute>} />
          <Route path="/Settings"  element={<ProtectedRoute><UserConfig /></ProtectedRoute>} />
          <Route path="/EditCarpeta" element={<ProtectedRoute><EditCarpeta /></ProtectedRoute>} />
          <Route path="/Compartits" element={<ProtectedRoute><Compartits /></ProtectedRoute>} />
          <Route path="*"          element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
