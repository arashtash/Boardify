import {Navigate, useRoutes} from "react-router-dom";
import LogoOnlyLayout from "./layouts/LogoOnlyLayout";
import Login from "./pages/login";
import Register from "./pages/register";
import Workspace from "./pages/workspace";
import ResetPassword from "./pages/reset-password";


export default function Router() {
    return useRoutes([
        {
            path: "/",
            element: <LogoOnlyLayout />,
            children: [
                {
                    index: true,
                    element: <Navigate to="/login" />,
                },
                {
                    path: "login",
                    element: <Login />,
                },
                {
                    path: "register",
                    element: <Register />
                },
                {
                    path: "workspace",
                    element: <Workspace />
                },
                {
                    path: "forgot-password",
                    element: <ResetPassword />
                },
            ],
        },
    ]);
}
