import * as Yup from "yup";
import {Button, Container, TextField, Typography} from "@mui/material";
import {FormikProvider, useFormik} from "formik";
import {LoadingButton} from "@mui/lab";
import {useNavigate} from "react-router-dom";
import httpClient from "../lib/httpClient";

export default function Login() {

  const LoginSchema = Yup.object().shape({
    email: Yup.string()
        .required("Email is required"),
    password: Yup.string()
        .required("Password is required")
  })

  const navigate = useNavigate();

  const formik = useFormik({
    initialValues: {
      email: '',
      password: ''
    },
    validationSchema: LoginSchema,
    onSubmit: async (values) => {
      const {email, password} = values;
      console.log(email, password);
      let message = null;
      try {
        message = await httpClient.post("http://localhost:8080/users/login", {email, password});
      } catch (e) {
        console.error(e);
      }
      console.log(message);
      console.log(message.data)
      if (message.data === -1) {
        alert("Invalid email/password")
      }else {
        localStorage.setItem("loggedIn", "true")
        localStorage.setItem("userID", message.data)
        navigate("/workspace")
      }
    },
  });

  const {errors, touched, isSubmitting, handleSubmit, getFieldProps} = formik;

  const ForgotPasswordButton = () =>{
    navigate("/forgot-password");
  }
  const RegisterButton = () =>{
    navigate("/register");
  }
  return (
      <>
        <Typography>Login</Typography>
        <Container>
          <FormikProvider value={formik}>
            <form autoComplete={"off"} noValidate onSubmit={handleSubmit}>
              <TextField fullWidth type={"email"} label={"Email"} {...getFieldProps("email")}
                         error={Boolean(touched.email && errors.email)} helperText={touched.email && errors.email}/>
              <TextField fullWidth type={"password"} label={"Password"}{...getFieldProps("password")}
                         error={Boolean(touched.password && errors.password)}
                         helperText={touched.password && errors.password}/>
              <LoadingButton type={"submit"} loading={isSubmitting} fullWidth
                             variant={"contained"}>Login</LoadingButton>
              <Button onClick={RegisterButton}>Register</Button>
              <Button onClick={ForgotPasswordButton}>Forgot Password</Button>
            </form>
          </FormikProvider>
        </Container>
      </>
  )
}