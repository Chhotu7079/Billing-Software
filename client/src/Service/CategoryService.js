import axios from "axios";

export const addCategory = async (category) => {
    return await axios.post('http://localhost:8484/api/v1.0/admin/categories', category, {headers: {"Authorization" : `Bearer ${localStorage.getItem('token')}`}});
}

export const deleteCategory = async (categoryId) => {
    return await axios.delete(`http://localhost:8484/api/v1.0/admin/categories/${categoryId}`, {headers: {"Authorization" : `Bearer ${localStorage.getItem('token')}`}});
}

export const fetchCategories = async () => {
    return await axios.get('http://localhost:8484/api/v1.0/categories', {headers: {"Authorization" : `Bearer ${localStorage.getItem('token')}`}});
}