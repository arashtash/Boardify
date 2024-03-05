import {createSlice} from "@reduxjs/toolkit";

const initialState = {
    details: {
        data: {},
        isFetching: false,
    },
    create: {
        data: null,
        isFetching: false,
    },
};

const userSlice = createSlice({
    name: "user",
    initialState,
    reducers: {
        setCreate: (state, action) => {
            state.create.data = action.payload;
        },
    },
});

export const { setCreate } = userSlice.actions;

export default userSlice.reducer;
