import { useContext, useState } from "react";
import "./DisplayItem.css";
import { AppContext } from "../../context/AppContext";
import Item from "../Item/Item";
import SearchBox from "../SearchBox/SearchBox";

const DisplayItem = ({ selectedCategory }) => {
  const { itemsData } = useContext(AppContext);

  //   console.log("itemData:", itemsData);

  const [searchText, setSearchText] = useState("");

  const filteredItems = itemsData
    .filter((item) => {
      if (!selectedCategory) return true;
      return item.categoryId === selectedCategory;
    })
    .filter((item) =>
      item.name.toLowerCase().includes(searchText.toLowerCase())
    );

  return (
    <div className="p-3">
      <div className="d-flex justify-content-between align-items-centers mb-4">
        <div></div>
        <div>
          <SearchBox onSearch={setSearchText} />
        </div>
      </div>
      <div className="row g-3">
        {filteredItems.length === 0 ? (
          <div className="col-12 text-center">
            <p>No content found</p>
          </div>
        ) : (
          filteredItems.map((item, index) => (
            <div key={index} className="col-md-4 col-sm-6">
              <Item
                itemName={item.name}
                itemPrice={item.price}
                itemImage={item.imgUrl}
                itemId={item.itemId}
              />
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default DisplayItem;
