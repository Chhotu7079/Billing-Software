import { useContext, useState } from "react";
import "./Explore.css";
import { AppContext } from "../../context/AppContext";
import DisplayCategory from "../../Components/DisplayCategory/DisplayCategory";
import DisplayItem from "../../Components/DisplayItems/DisplayItem";
import CustomerForm from "../../Components/CustomerForm/CustomerForm";
import CartItem from "../../Components/CartItems/CartItem";
import CartSummary from "../../Components/CartSummary/CartSummary";

const Explore = () => {
  const { categories } = useContext(AppContext);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [customerName, setCustomerName] = useState("");
  const [mobileNumber, setMobileNumber] = useState("");
  console.log(categories);
  return (
    <div className="explore-container text-light">
      <div className="left-column">
        <div className="first-row" style={{ overflowY: "auto" }}>
          <DisplayCategory
            selectedCategory={selectedCategory}
            setSelectedCategory={setSelectedCategory}
            categories={categories}
          />
        </div>

        <hr className="horizontal-line" />

        <div className="second-row" style={{ overflowY: "auto" }}>
          <DisplayItem selectedCategory={selectedCategory} />
        </div>
      </div>

      <div className="right-column d-flex flex-column">
        <div className="customer-form-container" style={{ height: "15%" }}>
          <CustomerForm
            customerName={customerName}
            mobileNumber={mobileNumber}
            setCustomerName={setCustomerName}
            setMobileNumber={setMobileNumber}
          />
        </div>

        <hr className="my-3 text-light" />

        <div
          className="cart-items-container"
          style={{ height: "55%", overflowY: "auto" }}
        >
          <CartItem />
        </div>

        <div className="cart-summary-container" style={{ height: "30%" }}>
          <CartSummary
            customerName={customerName}
            mobileNumber={mobileNumber}
            setCustomerName={setCustomerName}
            setMobileNumber={setMobileNumber}
          />
        </div>
      </div>
    </div>
  );
};

export default Explore;
