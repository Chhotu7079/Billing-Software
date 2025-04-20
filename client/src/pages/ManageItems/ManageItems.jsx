import ItemForm from '../../Components/ItemForm/ItemForm';
import ItemList from '../../Components/ItemList/ItemList';
import './ManageItem.css'

const ManageItems = () => {
    return (
        <div className="items-container text-light">
            <div className="left-column">
                <ItemForm/>
            </div>

            <div className="right-cloumn">
                <ItemList/>
            </div>
        </div>
    )
}

export default ManageItems;