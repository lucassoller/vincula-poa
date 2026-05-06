import Navbar from "./Navbar";

function Layout({ children }) {
  return (
    <div style={styles.navbarcontainer}>
      <Navbar />
        {children}
    </div>
  );
}

export default Layout;

const styles = {
    navbarcontainer: {
        display: "flex",
        flexDirection: "column",
        height: "100vh",
        fontFamily: "Segoe UI",
    }
};