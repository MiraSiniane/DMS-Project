// app/routes.jsx
import { lazy } from "react";
import { Navigate } from "react-router-dom";
import AuthGuard from "./auth/AuthGuard";
import { authRoles } from "./auth/authRoles";
import Loadable from "./components/Loadable";
import MatxLayout from "./components/MatxLayout/MatxLayout";
import sessionRoutes from "./views/sessions/session-routes";
import materialRoutes from "app/views/material-kit/MaterialRoutes";

// Lazy-loaded components
const AppEchart = Loadable(lazy(() => import("app/views/charts/echarts/AppEchart")));
const Analytics = Loadable(lazy(() => import("app/views/dashboard/Analytics")));
const Users = Loadable(lazy(() => import("app/views/users/Users")));
const UserDetails = Loadable(lazy(() => import("app/views/Users/UserDetails")));
const AddUser = Loadable(lazy(() => import("app/views/Users/AddUser")));
const EditUser = Loadable(lazy(() => import("app/views/Users/EditUser")));

const Files = Loadable(lazy(() => import("app/views/Files/Files")));
const FileDetails = Loadable(lazy(() => import("app/views/files/FileDetailView")));
const FolderContents = Loadable(lazy(() => import("app/views/Files/FolderContents")));

const routes = [
  { path: "/", element: <Navigate to="dashboard/default" /> },
  {
    element: (
      <AuthGuard>
        <MatxLayout />
      </AuthGuard>
    ),
    children: [
      ...materialRoutes,
      { path: "/dashboard/default", element: <Analytics />, auth: authRoles.admin },
      { path: "/charts/echarts", element: <AppEchart />, auth: authRoles.editor },
      { path: "/users", element: <Users />, auth: authRoles.admin },
      { path: "/users/:id", element: <UserDetails />, auth: authRoles.admin },
      {
        path: "/users/add",
        element: <AddUser onAddUser={(newUser) => setUsers([...users, newUser])} />,
        auth: authRoles.admin,
      },
      {
        path: "/users/edit/:id",
        element: <EditUser onEditUser={(updatedUser) => handleEditUser(updatedUser)} />,
        auth: authRoles.admin,
      },

      {
        path: "/files",
        element: <Files />,
        auth: authRoles.admin,
      },
      {
        path: "/files/folder/:folderId",
        element: <FolderContents />,
        auth: authRoles.admin,
      },
      {
        path: "/files/:id",
        element: <FileDetails />,
        auth: authRoles.admin,
      }
    ],
  },
  ...sessionRoutes,
];

export default routes;