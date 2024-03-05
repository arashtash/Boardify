import {ToastContainer} from 'react-toastify';
import './App.css';
import {BrowserRouter} from "react-router-dom";
import Router from "./routes";
import {store} from "./store";
import {Provider} from "react-redux";

function App() {
  return (
      <>
        <Provider store={store}>
          <ToastContainer />
          <BrowserRouter>
            <Router />
          </BrowserRouter>
        </Provider>
      </>
  );
}


export default App;
