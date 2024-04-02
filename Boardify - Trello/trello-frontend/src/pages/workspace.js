import React, { useEffect, useState, useMemo } from 'react';
import './WorkspacePage.css';
import {useNavigate} from "react-router-dom";
import {Button, Container, TextField} from "@mui/material";
import {FormikProvider, useFormik} from "formik";
import {LoadingButton} from "@mui/lab";
import * as Yup from "yup";
import httpClient from "../lib/httpClient";


export default function Workspace() {
    const navigate = useNavigate();
    const loggedIn = localStorage.getItem('loggedIn') === 'true';
    let userID = localStorage.getItem("userID");

    useEffect(() => {
        if (!loggedIn) {
            navigate('/login');
            alert("You must be logged in to access this page");
        }
    }, [loggedIn, navigate]);

    const NewWorkspaceSchema = Yup.object().shape({
        title: Yup.string()
            .required("Name can't be empty")
    })

    const refreshWorkspaces = () => {
        httpClient.get("http://localhost:8080/workspaces/getAll")
          .then(response => {
            setWorkspaces(response.data);
          })
          .catch(error => {
            console.error(error);
          });
      };

    const formik = useFormik({
        initialValues: {
            title: '',
            created: new Date().toISOString(),
            lastUpdated: new Date().toISOString(),
            userID: parseInt(userID)
        },
        validationSchema: NewWorkspaceSchema,
        onSubmit: async (values) => {
            const {title, created, lastUpdated, userID} = values;
            console.log(title, created, lastUpdated, userID);
            let message = null;
            try {
                message = await httpClient.post("http://localhost:8080/workspaces/create", {title, created, lastUpdated, userID});
                refreshWorkspaces(); // Refresh workspaces whenever a new workspace is created
            } 
            catch (e) {
                console.error(e);
            }
            console.log(message.data);
            navigate("/workspace");
            return message.data;

        },
    });
    const {errors, touched, isSubmitting, handleSubmit, getFieldProps } = formik;

    const LogoutButton = () =>{
        localStorage.setItem("loggedIn", "false")
        navigate("/login");
    }

    const [workspaces, setWorkspaces] = useState([]);
    const [boardTasks, setBoardTasks] = useState({});

    useEffect(() => {
        httpClient.get("http://localhost:8080/workspaces/getAll")
            .then(response => {
                setWorkspaces(response.data);
            })
            .catch(error => {
                console.error(error);
            });

            httpClient.get("http://localhost:8080/tasks/getAll")
            .then(response => {
                // list the tasks by boardId
                const tasksByBoardId = response.data.reduce((acc, task) => {
                const boardId = task.board.id;
                acc[boardId] = [...(acc[boardId] || []), task];
                return acc;
                }, {});

                setBoardTasks(tasksByBoardId);
            })
            .catch(error => {
                console.error(error);
            });
    }, []);


    const [emailInput, setEmailInput] = useState('');
    const [selectedWorkspaceId, setSelectedWorkspaceId] = useState(null);
    const [workspaceUsers, setWorkspaceUsers] = useState({});

    

    const getWorkspaceUsers = async (workspaceId) => {
        try {
            const response = await httpClient.get(`http://localhost:8080/workspaces/${workspaceId}/users`);
            return response.data;
        } 
        catch (error) {
            console.error(error);
            return [];
        }
    };

    useEffect(() => {
        httpClient
          .get('http://localhost:8080/workspaces/getAll')
          .then(async (response) => {
            setWorkspaces(response.data);
    
            // Get the users list for each workspace and store them in the workspaceUsers object
            const workspacesWithUsers = {};
            for (const workspace of response.data) {
              const users = await getWorkspaceUsers(workspace.id);
              workspacesWithUsers[workspace.id] = users;
            }
            setWorkspaceUsers(workspacesWithUsers);
          })
          .catch((error) => {
            console.error(error);
          });
      }, []);


      const addUserToWorkspace = async () => {
        if (!emailInput) {
          alert("Please enter an email address");
          return;
        }
      
        try {

            const selectedWorkspaceUsers = workspaceUsers[selectedWorkspaceId];
            if (selectedWorkspaceUsers && selectedWorkspaceUsers.some(user => user.email === emailInput)) {
        alert("User is already a member of this workspace");
      return;
    }

          await httpClient.post(`http://localhost:8080/workspaces/addMemberToWorkSpace?workspaceId=${selectedWorkspaceId}&email=${emailInput}`);
      
          // Get and update the users for the workspace
          const users = await getWorkspaceUsers(selectedWorkspaceId);
      
          // Update workspaceUsers with the new user for the respective workspace ID
          setWorkspaceUsers((prevWorkspaceUsers) => ({
            ...prevWorkspaceUsers,
            [selectedWorkspaceId]: users,
          }));
      
          refreshWorkspaces(); // Refresh workspaces if the user was successfully added to one of them
        } catch (error) {
          console.error(error);
          alert("Email does not exist");
        }
      
        // Clearing the entered email and workspace ID after adding the user
        setEmailInput("");
        setSelectedWorkspaceId(null);
      };

      
      const createBoard = async (workspaceId, boardTitle) => {
        // Validating workspaceId
        if (!Number.isInteger(workspaceId) || workspaceId <= 0) {
          alert('Invalid workspaceId');
          return;
        }
      
        try {
          const newBoard = {
            title: boardTitle,
            created: new Date().toISOString(),
            lastUpdated: new Date().toISOString(),
          };
      
          // Pass both boardTitle and workspaceId request parameters
          const response = await httpClient.post(
            `http://localhost:8080/boards/create?workspaceId=${workspaceId}&boardName=${boardTitle}`,
            newBoard
          );
          const createdBoard = response.data;
          // Update the frontend state to include the new board
          setWorkspaces((prevWorkspaces) => {
            const updatedWorkspaces = prevWorkspaces.map((workspace) => {
              if (workspace.id === workspaceId) {
                return {
                  ...workspace,
                  boards: [...workspace.boards, createdBoard],
                };
              }
              return workspace;
            });
            return updatedWorkspaces;
          });
          setShowBoardForm(false);

          refreshWorkspaces(); // Refresh workspaces if the board was successfully added to one of them
        } catch (error) {
          console.error(error);
          alert('Board creation failed');
        }

        setSelectedWorkspaceId(null);
      };
      
      
      
      
      
      
      
    
      const [showBoardForm, setShowBoardForm] = useState(false);
      const [boardTitle, setBoardTitle] = useState('');
      

      const handleAddBoardClick = (workspaceId) => {
        setSelectedWorkspaceId(workspaceId);
        setShowBoardForm(true);
      };


      const deleteBoard = async (boardId) => {
        try {
          await httpClient.delete(`http://localhost:8080/boards/delete?id=${boardId}`);
          // Refresh the workspace data to get the updated board list
          refreshWorkspaces();
        } catch (error) {
          console.error(error);
          alert('Failed to delete the board');
        }
      };


      //PHASE 3: TASKS:

      const [taskName, setTaskName] = useState('');
      const [taskDueDate, setTaskDueDate] = useState('');

      

      

      const createTask = async (boardId) => {
        //If task name or due date are not entered
        if (!taskName || !taskDueDate) {
          alert('Please enter task name and due date');
          return;
        }

        // Get the current date and time
        const currentDate = new Date(Date.UTC(new Date().getUTCFullYear(), new Date().getUTCMonth(), new Date().getUTCDate()));
        const dueDate = new Date(taskDueDate);

        if (dueDate < currentDate) {
          alert('Due date cannot be in the past');
          return;
        }
      
        try {
          const response = await httpClient.post(`http://localhost:8080/tasks/createTask`, {
            name: taskName,
            boardId: boardId,
            date: taskDueDate,
          });
      
          // Assuming response.data contains the created task object
          console.log('Created task:', response.data);
      
          // Refresh the boards after creating tasks
          setBoardTasks((prevBoardTasks) => {
            const updatedBoardTasks = { ...prevBoardTasks };
            updatedBoardTasks[boardId] = [...(updatedBoardTasks[boardId] || []), response.data];
            return updatedBoardTasks;
          });

        } catch (error) {
          console.error('Error in task creation:', error);
        }
      
        // Clear the form input after task creation
        setTaskName('');
        setTaskDueDate('');
      };

      const changeTaskStatus = async (taskId, newStatus) => {
        try {
          await httpClient.put(`http://localhost:8080/tasks/${taskId}/status?newStatus=${newStatus}`);
          
          httpClient.get("http://localhost:8080/tasks/getAll")
            .then(response => {
                // list the tasks by boardId
                const tasksByBoardId = response.data.reduce((acc, task) => {
                const boardId = task.board.id;
                acc[boardId] = [...(acc[boardId] || []), task];
                return acc;
                }, {});

                setBoardTasks(tasksByBoardId);
            })
            .catch(error => {
                console.error(error);
            });


        } catch (error) {
          console.error('Error changing task status:', error);
        }
      };
      
      const [selectedBoardId, setSelectedBoardId] = useState(null);
      const [showTaskForm, setShowTaskForm] = useState(false);

      const addTaskClick = (boardId) => {
        setSelectedBoardId(boardId);
        setShowTaskForm(true);
      };


      const [selectedTaskId, setSelectedTaskId] = useState(null);
    const [taskEmailInput, setTaskEmailInput] = useState('');
    const [showTaskAddUserForm, setShowTaskAddUserForm] = useState(false);
      
    const addUserTaskClick = (taskId) => {
        setSelectedTaskId(taskId);
        setShowTaskAddUserForm(true);
      };

      const refreshTasks = async () => {
        try {
          const response = await httpClient.get("http://localhost:8080/tasks/getAll");
          // List the tasks by boardId
          const tasksByBoardId = response.data.reduce((acc, task) => {
            const boardId = task.board.id;
            acc[boardId] = [...(acc[boardId] || []), task];
            return acc;
          }, {});
      
          setBoardTasks(tasksByBoardId);
        } catch (error) {
          console.error(error);
        }
      };

      const addTaskUser = async (event) => {
        event.preventDefault();
        if (!emailInput) {
          alert('Please enter an email address');
          return;
        }
      
        try {
          await httpClient.post(
            `http://localhost:8080/tasks/${selectedTaskId}/add-member?email=${emailInput}`
          );
          setEmailInput('');
          setSelectedTaskId(null);
          setShowTaskAddUserForm(false);

          // Refresh tasks after adding user to the task
            refreshTasks();
        } catch (error) {
          console.error(error);
          alert('Error adding user to task. Please make sure the email is valid and the user is a member of the workspace.');
        }
      };




      //SEARCH AND FILTER:
      
      //Comparison functions for filter
      const dueTodayFilter = (task) => {
        const today = new Date(Date.UTC(new Date().getUTCFullYear(), new Date().getUTCMonth(), new Date().getUTCDate()));
        const taskDueDate = new Date(task.dueDate);
        return taskDueDate.toDateString() === today.toDateString();
      };
      
      const dueInThisWeekFilter = (task) => {
        const today = new Date(Date.UTC(new Date().getUTCFullYear(), new Date().getUTCMonth(), new Date().getUTCDate()));
        const nextWeek = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
        const taskDueDate = new Date(task.dueDate);
        return taskDueDate >= today && taskDueDate <= nextWeek;
      };
      
      const overdueFilter = (task) => {
        const today = new Date(Date.UTC(new Date().getUTCFullYear(), new Date().getUTCMonth(), new Date().getUTCDate()));
        const taskDueDate = new Date(task.dueDate);
        return taskDueDate < today;
      };


      const [searchTerm, setSearchTerm] = useState("");
      const [searchVisible, setSearchVisible] = useState(false);
      const [filterVisible, setFilterVisible] = useState(false);

      const handleSearchChange = (event) => {
        setSearchTerm(event.target.value.toLowerCase());
      };

      
      

      const [selectedFilter, setSelectedFilter] = useState("all");

      const handleFilterChange = (event) => {
        setSelectedFilter(event.target.value);
      };

      
      const filteredTasks = useMemo(() => {
        const lowerCasedSearchTerm = searchTerm.toLowerCase();
        return workspaces.reduce((filteredTasks, workspace) => {
          const filteredBoards = workspace.boards.map((board) => ({
            ...board,
            tasks: boardTasks[board.id] &&
            boardTasks[board.id].filter(
              (task) =>
                task.name.toLowerCase().includes(lowerCasedSearchTerm) &&
                (selectedFilter === "all" ||
                  (selectedFilter === "dueToday" && dueTodayFilter(task)) ||
                  (selectedFilter === "dueInThisWeek" && dueInThisWeekFilter(task)) ||
                  (selectedFilter === "overdue" && overdueFilter(task)))
            ),
        }));
      
          return [...filteredTasks, { ...workspace, boards: filteredBoards }];
        }, []);
      }, [workspaces, boardTasks, searchTerm, selectedFilter]);


      

      const [searchAndFilterVisible, setSearchAndFilterVisible] = useState(false);

      // Function to toggle the visibility of the search and filter section
      const toggleSearchAndFilter = () => {
        setSearchAndFilterVisible((prevVisible) => !prevVisible);
      };

      
    


    return (
        <>
            <Button onClick={LogoutButton}>Logout</Button>
            <h1>Boardify</h1>
            <h4>Welcome User #{localStorage.getItem("userID")}</h4>
            <h4>Please note some forms appear under the page. You may need to scroll down if there are many workspaces, boards and tasks. Also tasks' status can only be changed step by step as described in the lab.</h4>
            <h2>Your Workspaces:</h2>

            <Container>
                <FormikProvider value={formik}>
                    <form autoComplete={"off"} noValidate onSubmit={handleSubmit}>
                        <TextField fullWidth label={"Workspace Name"} {...getFieldProps("title")}
                                   error={Boolean(touched.title && errors.title)} helperText={touched.title && errors.title}/>
                        <LoadingButton type={"submit"} loading={isSubmitting} fullWidth
                                       variant={"contained"}>Create a Workspace</LoadingButton>
                    </form>
                </FormikProvider>
            </Container>

            <Container>
                <div>
                    {workspaces.map(workspace => (
                        <div class = "workspace" key={workspace.id}>
                            <h2>{workspace.title}</h2>
                            <p>Created: {workspace.created}</p>
                            <button onClick={() => handleAddBoardClick(workspace.id)}>Add Board</button>
                            <button onClick={() => setSelectedWorkspaceId(workspace.id)}>Add Member</button>
                            
                            <div class= "workspace-member">
                                <h4>Workspace Users:</h4>
                                {workspaceUsers[workspace.id]?.map((user) => (
                                <p class = "workspace-members" key={user.id}>{user.email}</p>
                            ))}
                            </div>
                            <div>
                                <h4>Boards:</h4>
                                {workspace.boards &&
                                workspace.boards.map((board) => (
                                    <div key={board.id}>

                                    
                                        <p>{board.title}</p>
                                        <button class = "remove-button" onClick={() => deleteBoard(board.id)}>Delete</button>
                                        <button onClick={() => addTaskClick(board.id)}>Add Task</button>

                                        {boardTasks[board.id] && boardTasks[board.id].map(task => (
                                            <div key={task.id} class="tasks">
                                                <p>{task.name}</p>
                                                <p>Due Date: {new Date(task.dueDate).toLocaleDateString(undefined, { timeZone: 'UTC' })}</p>
                                                <p>Status: {task.status}</p>
                                                <select value={task.status} onChange={(e) => changeTaskStatus(task.id, e.target.value)}>
                                                    <option value="TODO">TODO</option>
                                                    <option value="DOING">DOING</option>
                                                    <option value="DONE">DONE</option>
                                                    
                                                </select>
                                                <button onClick={() => addUserTaskClick(task.id)}>Add User</button>
                                                <div>
                                                    <h4>Task Members:</h4>
                                                    {task.users &&
                                                    task.users.map((user) => (
                                                        <p key={user.id} class="task-member">
                                                        {user.email}
                                                        </p>
                                                    ))}
                                                </div>
                                            </div>
                                        ))}
                                        {showTaskForm && selectedBoardId === board.id && (
                        
                                        <div>
                                        <h2>Create Task</h2>

                                        <TextField
                                            fullWidth
                                            label="Task Name"
                                            value={taskName}
                                            onChange={(e) => setTaskName(e.target.value)}
                                        />

                                        <TextField
                                            fullWidth
                                            label="Due date"
                                            type="date"
                                            value={taskDueDate}
                                            onChange={(e) => setTaskDueDate(e.target.value)}
                                        />

                                        <Button onClick={() => createTask(selectedBoardId)}>
                                            Create Task
                                        </Button>
                                        <Button
                                            onClick={() => {
                                            setSelectedBoardId(null);
                                            setShowTaskForm(false);
                                            }}
                                        >
                                            Cancel
                                        </Button>
                                        </div>
                                    )}
                                    

                                  </div>
                                
                                
                            ))}
                            </div>
                        </div>
                    ))}
                </div>
                
            </Container>
            {selectedWorkspaceId !== null && (
                <div>
                <div>
                <h2>Add User to Workspace</h2>
                <TextField fullWidth label={"Email"} value={emailInput} onChange={(e) => setEmailInput(e.target.value)} />
                <Button onClick={addUserToWorkspace}>Add</Button>
                <Button onClick={() => setSelectedWorkspaceId(null)}>Cancel</Button>
                </div>
                </div>
            )}
            {showBoardForm && (
                <div>
                  <h2>Create Board</h2>
                  <TextField
                    fullWidth
                    label={'Board Title'}
                    value={boardTitle}
                    onChange={(e) => setBoardTitle(e.target.value)}
                  />
                  <Button onClick={() => createBoard(selectedWorkspaceId, boardTitle)}>Create</Button>
                  <Button onClick={() => setShowBoardForm(false)}>Cancel</Button>
                </div>
              )}
              {showTaskAddUserForm && selectedTaskId && (
                <form onSubmit={addTaskUser}>
                    <input
                    type="text"
                    placeholder="Enter email"
                    value={emailInput}
                    onChange={(e) => setEmailInput(e.target.value)}
                    />
                    <button type="submit">Add User</button>
                    <button onClick={() => setShowTaskAddUserForm(false)}>Cancel</button>
                </form>
            )}

<Button class = "search" onClick={toggleSearchAndFilter}>
        {searchAndFilterVisible ? "Close Search and Filter" : "Show Search and Filter"}
      </Button>

      

            {searchAndFilterVisible && (
        <>
          <TextField fullWidth label="Search Tasks" value={searchTerm} onChange={handleSearchChange} />
          {filteredTasks.map((workspace) => (
            <div className="workspace" key={workspace.id}>
              <h2>{workspace.title}</h2>
              

              {workspace.boards &&
                workspace.boards.map((board) => (
                  <div key={board.id}>
                    <p>{board.title}</p>
                    

                    {board.tasks &&
                      board.tasks.map((task) => (
                        <div key={task.id} className="tasks">
                          <p>{task.name}</p>
                          <p>Due Date: {new Date(task.dueDate).toLocaleDateString(undefined, { timeZone: 'UTC' })}</p>
                          <p>Status: {task.status}</p>
                          
                        </div>
                      ))}
                  </div>
                ))}
            </div>
          ))}
          <div>
            <h3>Filter Tasks:</h3>
            <label>
              <input
                type="radio"
                value="all"
                checked={selectedFilter === "all"}
                onChange={handleFilterChange}
              />
              All
            </label>
            <label>
              <input
                type="radio"
                value="dueToday"
                checked={selectedFilter === "dueToday"}
                onChange={handleFilterChange}
              />
              Due Today
            </label>
            <label>
              <input
                type="radio"
                value="dueInThisWeek"
                checked={selectedFilter === "dueInThisWeek"}
                onChange={handleFilterChange}
              />
              Due in This Week
            </label>
            <label>
              <input
                type="radio"
                value="overdue"
                checked={selectedFilter === "overdue"}
                onChange={handleFilterChange}
              />
              Overdue
            </label>
          </div>
        </>
      )}
    </>
  );
}
