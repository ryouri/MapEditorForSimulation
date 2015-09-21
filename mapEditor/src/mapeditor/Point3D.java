package mapeditor;

public class Point3D {
	 public int x;
	 public int y;
	 public int z;
	 public Point3D(int x, int y, int z){
	 	this.x = x;
	 	this.y = y;
	 	this.z = z;
	 }

	 @Override
	 public boolean equals(Object o) {
		 if (o instanceof Point3D){
			 Point3D po = (Point3D) o;
			 if (po.x == this.x && po.y == this.y && po.z == this.z){
				 return true;
			 }
		 }
		 return false;
	 }
	 
	 @Override
	 public int hashCode() {
		 return this.x * 1000 + this.y * 10 + this.z;
	 }
}
