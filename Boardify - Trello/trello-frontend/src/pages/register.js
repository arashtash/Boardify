import * as Yup from "yup";
import {Button, Container, TextField, Typography} from "@mui/material";
import {FormikProvider, useFormik} from "formik";
import {LoadingButton} from "@mui/lab";
//import {useDispatch} from "react-redux";
//import {toast} from "react-toastify";
import {useNavigate} from "react-router-dom";
//import storage from "../lib/localStorage";
//import {createUser} from "../store/slices/user/UserSlice";
import httpClient from "../lib/httpClient";

export default function Register(){

  const RegisterSchema = Yup.object().shape({
    email: Yup.string()
        .email("Email must be a valid email address")
        .required("Email is required"),
    password: Yup.string()
        .min(8, "Password must be at least 8 characters")
        .matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^a-zA-Z0-9]).+$/,
            'Password must contain at least 1 uppercase character, 1 lowercase character, 1 number, and 1 special character'
        )
        .required("Password is required"),
    passwordConfirmation: Yup.string().oneOf(
        [Yup.ref("password"), null],
        "Passwords must match"),
    securityAnswer: Yup.string()
        .min(3, "Must be at least 3 characters")
        .max(15, "Must be at most 15 characters")
        .required("Security answer is required")
  })

  const navigate = useNavigate();

  const formik = useFormik({
    initialValues: {
      email: '',
      password: '',
      passwordConfirmation: '',
      securityAnswer: '',
    },
    validationSchema: RegisterSchema,
    onSubmit: async (values) => {
      const {email, password, securityAnswer} = values;
      console.log(email, password, securityAnswer);
      let user = null;
      try {
        user = await httpClient.post("http://localhost:8080/users/create", { email, password, securityAnswer });
      } catch (e) {
        console.error(e);
      }
      console.log(user.data);
      navigate("/login");
      return user.data;

    },
  });

  const {errors, touched, isSubmitting, handleSubmit, getFieldProps } = formik;

  const LoginButton = () =>{
    navigate("/login");
  }

  return (
      <>
        <Typography>Register</Typography>
          <Container>
            <FormikProvider value={formik}>
              <form autoComplete={"off"} noValidate onSubmit={handleSubmit}>
                <TextField fullWidth type={"email"} label={"Email"} {...getFieldProps("email")}
                           error={Boolean(touched.email && errors.email)} helperText={touched.email && errors.email}/>
                <TextField fullWidth type={"password"} label={"Password"}{...getFieldProps("password")}
                           error={Boolean(touched.password && errors.password)} helperText={touched.password && errors.password}/>
                <TextField fullWidth type={"password"} label={"Confirm Password"}{...getFieldProps("passwordConfirmation")}
                           error={Boolean(touched.passwordConfirmation && errors.passwordConfirmation)} helperText={touched.passwordConfirmation && errors.passwordConfirmation}/>
                <TextField fullWidth label={"Security Question: What is your favorite color?"}{...getFieldProps("securityAnswer")}
                           error={Boolean(touched.securityAnswer && errors.securityAnswer)} helperText={touched.securityAnswer && errors.securityAnswer}/>
                <LoadingButton type={"submit"} loading={isSubmitting} fullWidth variant={"contained"}>Register</LoadingButton>
                <Button onClick={LoginButton}>Back to Login Page</Button>
              </form>
            </FormikProvider>
          </Container>
      </>
  )
}