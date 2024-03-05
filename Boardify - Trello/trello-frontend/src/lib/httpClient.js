import axios from 'axios';
import storage from "./localStorage";
import {setCreate} from "../store/slices/user/UserSlice";
import {store} from "../store";

const httpClient = axios.create({
    baseURL: process.env.REACT_APP_SERVER_BASE_URL,
});

httpClient.interceptors.request.use(config => {
    const token = storage.get("token");
    if(token){
        config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
});

httpClient.interceptors.response.use(
    (config) => {
        console.log('Response Interceptor - Success:', config);
        config.data = JSON.parse(JSON.stringify(config.data));
        return config
    },
    (error) => {
        console.log('Response Interceptor - Error:', error);
        if (error && error.response.status === 401) {
            storage.remove("token");
            store.dispatch(setCreate(null));
        }

        return Promise.reject(error.response ? error.response.data : error);
    }
);

export default httpClient;
