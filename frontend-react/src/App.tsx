import { BrowserRouter, Routes, Route } from 'react-router';
import { Toaster } from 'react-hot-toast';
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
import './App.css';

function App() {
  return (
    <><Toaster position="top-center" /><BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/home" element={<Home />} />
        <Route path="/Item" element={<Item />} />
        <Route path="/AddItem" element={<AddItem />} />
        <Route path="/ChooseType" element={<ChooseType />} />
        <Route path="/Items" element={<Items />} />
        <Route path="/EditItem" element={<EditItem />} />
        <Route path="/Carpetes" element={<Carpetas />} />
        <Route path="/Carpeta" element={<Carpeta />} />
        <Route path="/AddCarpeta" element={<AddCarpeta/>} />
      </Routes>
    </BrowserRouter></>
  );
}

export default App;
