<h1>Retail Billing Software</h1>

<p>This project is a comprehensive retail billing system developed for a real-world client, featuring all the essential components of a modern billing application. From category and product management to real-time billing and image storage via AWS S3, this system is designed to streamline retail operations.</p>

<h2>Features</h2>
<ul>
  <li><strong>Category & Product Management:</strong> Add, update, and manage product categories and their respective items.</li>
  <li><strong>File Upload (Image) Support:</strong> Upload product images and store them in AWS S3.</li>
  <li><strong>REST APIs with Spring Boot:</strong> Backend services for handling various billing and product operations.</li>
  <li><strong>Image Handling from AWS:</strong> Manage images through AWS S3 storage for better scalability and accessibility.</li>
  <li><strong>Dynamic UI Built with React + Bootstrap:</strong> A modern, responsive interface with real-time updates and smooth interactions.</li>
  <li><strong>Search and Delete Features:</strong> Easily search for products and delete records when necessary.</li>
  <li><strong>Responsive Design:</strong> A fully responsive design to ensure smooth operation across devices (mobile, tablet, desktop).</li>
</ul>

<h2>Technologies Used</h2>
<ul>
  <li><strong>Frontend:</strong>
    <ul>
      <li>React.js</li>
      <li>Bootstrap 5</li>
      <li>Axios</li>
    </ul>
  </li>
  <li><strong>Backend:</strong>
    <ul>
      <li>Spring Boot</li>
      <li>JPA (Java Persistence API)</li>
      <li>MySQL (Database)</li>
    </ul>
  </li>
  <li><strong>Build Tools:</strong>
    <ul>
      <li>Maven</li>
    </ul>
  </li>
  <li><strong>File Storage:</strong>
    <ul>
      <li>AWS S3 for image upload and management</li>
    </ul>
  </li>
</ul>

<h2>Installation</h2>

<h3>Clone the Repository</h3>
<pre><code>git clone https://github.com/Chhotu7079/Billing-Software.git
cd Billing-Software</code></pre>

<h3>Backend Setup (Spring Boot)</h3>
<ol>
  <li>Navigate to the backend directory and open the <code>application.properties</code> file.</li>
  <li>Set up your MySQL database connection (e.g., <code>spring.datasource.url</code>).</li>
  <li>Make sure you have a working AWS account with S3 configured for image storage.</li>
  <li>Build and run the backend:</li>
</ol>
<pre><code>cd backend
mvn clean install
mvn spring-boot:run</code></pre>

<h3>Frontend Setup (React)</h3>
<ol>
  <li>Navigate to the frontend directory:</li>
</ol>
<pre><code>cd frontend</code></pre>

<ol>
  <li>Install the required dependencies:</li>
</ol>
<pre><code>npm install</code></pre>

<ol>
  <li>Start the React development server:</li>
</ol>
<pre><code>npm start</code></pre>

<p>Your application should now be running locally. Visit <a href="http://localhost:5471" target="_blank">http://localhost:5471</a> in your browser to access the frontend.</p>

<h2>Usage</h2>
<ul>
  <li><strong>Admin Panel:</strong> The admin panel allows the management of product categories, products, and image uploads.</li>
  <li><strong>Product Listings:</strong> View products with their details, including images, prices, and availability.</li>
  <li><strong>Order Management:</strong> Add, update, and track orders in real-time.</li>
  <li><strong>Search and Filter:</strong> Efficient search and filtering capabilities to find products and manage orders quickly.</li>
</ul>

<h2>AWS Configuration</h2>
<p>For image storage, the application uses <strong>AWS S3</strong>. Ensure that you have your AWS credentials set up correctly and your S3 bucket is configured. Update the following fields in <code>application.properties</code>:</p>
<pre><code>aws.access.key.id=&lt;Your-AWS-Access-Key&gt;
aws.secret.access.key=&lt;Your-AWS-Secret-Key&gt;
aws.bucket.name=&lt;Your-S3-Bucket-Name&gt;</code></pre>

<h2>Screenshots</h2>
<p><strong>Dashboard</strong></p>
<img src="./screenshots/Screenshot 2025-04-20 104339.png" alt="Dashboard" />

<p><strong>Product Management</strong></p>
<img src="./screenshots/product-management.png" alt="Product Management" />

<h2>Contributing</h2>
<ol>
  <li>Fork the repository.</li>
  <li>Create a new branch (<code>git checkout -b feature-branch</code>).</li>
  <li>Commit your changes (<code>git commit -am 'Add new feature'</code>).</li>
  <li>Push to the branch (<code>git push origin feature-branch</code>).</li>
  <li>Open a pull request.</li>
</ol>

<h2>License</h2>
<p>This project is licensed under the MIT License - see the <a href="LICENSE">LICENSE</a> file for details.</p>
