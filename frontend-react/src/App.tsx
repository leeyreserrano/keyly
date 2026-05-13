import { BrowserRouter, Routes, Route, Navigate } from 'react-router';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';
import { CryptoProvider } from './context/CryptoContext';
import ProtectedRoute from './routes/ProtectedRoute';
import Layout from './components/Layout';
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
import Stadistics from './pages/Stadistics';
import Duplicats from './pages/Items/Duplicate';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <CryptoProvider>
        <Toaster position="top-center" />
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Login />} />
            <Route
              path="/*"
              element={
                <ProtectedRoute>
                  <Layout>
                    <Routes>
                      <Route path="/home"        element={<Home />} />
                      <Route path="/Item"        element={<Item />} />
                      <Route path="/AddItem"     element={<AddItem />} />
                      <Route path="/ChooseType"  element={<ChooseType />} />
                      <Route path="/Items"       element={<Items />} />
                      <Route path="/EditItem"    element={<EditItem />} />
                      <Route path="/Carpetes"    element={<Carpetas />} />
                      <Route path="/Carpeta"     element={<Carpeta />} />
                      <Route path="/AddCarpeta"  element={<AddCarpeta />} />
                      <Route path="/Settings"    element={<UserConfig />} />
                      <Route path="/EditCarpeta" element={<EditCarpeta />} />
                      <Route path="/Compartits"  element={<Compartits />} />
                      <Route path="/Stadistics"  element={<Stadistics />} />
                      <Route path="/Duplicats"   element={<Duplicats />} />
                      <Route path="*"            element={<Navigate to="/home" replace />} />
                    </Routes>
                  </Layout>
                </ProtectedRoute>
              }
            />
          </Routes>
        </BrowserRouter>
      </CryptoProvider>
    </AuthProvider>
  );
}

export default App;