import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import Menubar from "./Components/Menubar/Menubar";
import Dashboard from "./pages/Dashboard/Dashboard";
import ManageCategory from "./pages/ManageCategory/ManageCategory";
import ManageUsers from "./pages/ManageUsers/ManageUsers";
import ManageItems from "./pages/ManageItems/ManageItems";
import Explore from "./pages/Explore/Explore";
import { Toaster } from "react-hot-toast";
import Login from "./pages/Login/Login";
import OrderHistory from "./pages/OrderHistory/OrderHistory";
import { useContext } from "react";
import { AppContext } from "./context/AppContext";
import NotFound from "./pages/NotFound/NotFound";

const App = () => {
  const location = useLocation();

  const { auth } = useContext(AppContext);

  const LoginRoute = ({ element }) => {
    if (auth.token) {
      return <Navigate to="/dashboard" />;
    }
    return element;
  };

  const ProtectedRoute = ({ element, allowedRoles }) => {
    if (!auth.token) {
      return <Navigate to="/login" replace />;
    }

    if (allowedRoles && !allowedRoles.includes(auth.role)) {
      return <Navigate to="/dashboard" replace />;
    }
    return element;
  };

  return (
    <div>
      {location.pathname !== "/login" && <Menubar />}
      <Toaster />
      <Routes>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/explore" element={<Explore />} />

        {/* Admins only routes */}
        <Route path="/category" element={<ProtectedRoute element={<ManageCategory/>} allowedRoles={["ROLE_ADMIN"]}/>} />
        <Route path="/users" element={<ProtectedRoute element={<ManageUsers/>} allowedRoles={["ROLE_ADMIN"]}/>} />
        <Route path="/items" element={<ProtectedRoute element={<ManageItems/>} allowedRoles={["ROLE_ADMIN"]}/>} />

        <Route path="/login" element={<LoginRoute element={<Login />}/>} />
        <Route path="/orders" element={<OrderHistory />} />
        <Route path="/" element={<Login />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </div>
  );
};

export default App;
