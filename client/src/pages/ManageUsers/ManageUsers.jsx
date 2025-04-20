import { useEffect } from 'react';
import UserForm from '../../Components/UserForm/UserForm';
import UserList from '../../Components/UserLists/UserList';
import './ManageUsers.css'
import { useState } from 'react';
import toast from 'react-hot-toast';
import { fetchUser } from '../../Service/UserService';

const ManageUsers = () => {

    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);


    useEffect(() => {
        async function loadUsers() {
            try{
                setLoading(true);
                const response = await fetchUser();
                setUsers(response.data);
            }catch(err){
                console.error(err);
                toast.error("Unable to fetch users");
            }finally{
                setLoading(false);
            }
        }
        loadUsers();
    }, []);

    return (
        <div className="users-container text-light">
            <div className="left-column">
                <UserForm setUsers={setUsers}/>
            </div>

            <div className="right-cloumn">
                <UserList users={users} setUsers={setUsers}/>
            </div>
        </div>
    )
}

export default ManageUsers;