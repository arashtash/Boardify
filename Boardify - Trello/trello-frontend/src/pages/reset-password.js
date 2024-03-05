import * as Yup from "yup";
import {Button, Container, TextField, Typography} from "@mui/material";
import {FormikProvider, useFormik} from "formik";
import {LoadingButton} from "@mui/lab";
import {useNavigate} from "react-router-dom";
import httpClient from "../lib/httpClient";

export default function ResetPassword() {

    const ResetPasswordSchema = Yup.object().shape({
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
            securityAnswer: '',
            password: ''
        },
        validationSchema: ResetPasswordSchema,
        onSubmit: async (values) => {
            const {email, securityAnswer, password} = values;
            console.log(email, securityAnswer, password);
            let message = null;
            try {
                message = await httpClient.post("http://localhost:8080/users/change-password", {email, password, securityAnswer});
            } catch (e) {
                console.error(e);
            }
            console.log(message);
            if (message.data === "Password reset successful")
                navigate("/login");
            else
                alert(message.data)
        },
    });

    const {errors, touched, isSubmitting, handleSubmit, getFieldProps} = formik;

    const LoginButton = () =>{
        navigate("/login");
    }

    return (
        <>
            <Typography>ResetPassword</Typography>
            <Container>
                <FormikProvider value={formik}>
                    <form autoComplete={"off"} noValidate onSubmit={handleSubmit}>
                        <TextField fullWidth type={"email"} label={"Email"} {...getFieldProps("email")}
                                   error={Boolean(touched.email && errors.email)} helperText={touched.email && errors.email}/>
                        <TextField fullWidth label={"Security Question: What is your favorite color?"}{...getFieldProps("securityAnswer")}
                                   error={Boolean(touched.securityAnswer && errors.securityAnswer)} helperText={touched.securityAnswer && errors.securityAnswer}/>
                        <TextField fullWidth type={"password"} label={"New Password"}{...getFieldProps("password")}
                                   error={Boolean(touched.password && errors.password)}
                                   helperText={touched.password && errors.password}/>
                        <TextField fullWidth type={"password"} label={"Confirm Password"}{...getFieldProps("passwordConfirmation")}
                                   error={Boolean(touched.passwordConfirmation && errors.passwordConfirmation)} helperText={touched.passwordConfirmation && errors.passwordConfirmation}/>
                        <LoadingButton type={"submit"} loading={isSubmitting} fullWidth
                                       variant={"contained"}>Reset Password</LoadingButton>
                        <Button onClick={LoginButton}>Back to Login Page</Button>
                    </form>
                </FormikProvider>
            </Container>
        </>
    )
}