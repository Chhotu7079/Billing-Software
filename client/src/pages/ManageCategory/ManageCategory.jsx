import CategoryForm from '../../Components/CategoryForm/CategoryForm';
import CategoryList from '../../Components/CategoryList/CategoryList';
import './ManageCategory.css';
const ManageCategory = () => {
    return (
        <div className="category-container text-light">
            <div className="left-column">
                <CategoryForm/>
            </div>

            <div className="right-cloumn">
                <CategoryList/>
            </div>
        </div>
    )
}

export default ManageCategory;