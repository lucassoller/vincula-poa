import Navbar from "./Navbar";
import "./layout.css"

function Layout({ children }) {
  return (
    <div className="navbarcontainer">
      <Navbar />
        {children}
    </div>
  );
}

export default Layout;