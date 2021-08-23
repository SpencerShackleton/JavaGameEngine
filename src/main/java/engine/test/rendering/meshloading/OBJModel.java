package engine.test.rendering.meshloading;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import engine.test.core.Util;
import engine.test.core.math.vectors.Vector2f;
import engine.test.core.math.vectors.Vector3f;

public class OBJModel {

	private ArrayList<Vector3f> positions;
	private ArrayList<Vector2f> texCoords;
	private ArrayList<Vector3f> normals;
	private ArrayList<OBJIndex> indices;
	
	private boolean hasTexCoords;
	private boolean hasNormals;
	
	public OBJModel(String filePath) {
		this.positions = new ArrayList<>();
		this.texCoords = new ArrayList<>();
		this.normals = new ArrayList<>();
		this.indices = new ArrayList<>();
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(" ");
				tokens = Util.removeEmptyStrings(tokens);
				
				if (tokens.length == 0 || tokens[0].equals("#")) continue;
				else if (tokens[0].equals("v")) {
					
					positions.add(new Vector3f(Float.valueOf(tokens[1]), 
											   Float.valueOf(tokens[2]), 
											   Float.valueOf(tokens[3])));
				}
				else if (tokens[0].equals("vt")) {
					texCoords.add(new Vector2f(Float.valueOf(tokens[1]), 
							 				   Float.valueOf(tokens[2])));
				}
				else if (tokens[0].equals("vn")) {
					normals.add(new Vector3f(Float.valueOf(tokens[1]), 
											 Float.valueOf(tokens[2]), 
											 Float.valueOf(tokens[3])));
				}
				else if (tokens[0].equals("f")) {
					
					for (int i = 0; i < tokens.length - 3; i++) {
						indices.add(parseOBJIndex(tokens[1]));
						indices.add(parseOBJIndex(tokens[2 + i]));
						indices.add(parseOBJIndex(tokens[3 + i]));
					}
				}
			}
			
			reader.close();
			
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public IndexedModel toIndexedModel() {
		IndexedModel res = new IndexedModel();
		IndexedModel normalModel = new IndexedModel();
		
		HashMap<OBJIndex, Integer> resultIndexMap = new HashMap<>();
		HashMap<Integer, Integer> normalIndexMap = new HashMap<>();
		
		HashMap<Integer, Integer> indexMap = new HashMap<>();
		
		for (int i = 0; i < indices.size(); i++) {
			OBJIndex currentIndex = indices.get(i);
			
			Vector3f currentPosition = positions.get(currentIndex.vertexIndex);
			Vector2f currentTexCoord = new Vector2f(0,0);
			Vector3f currentNormal = new Vector3f(0,0,0);
			
			if (hasTexCoords)
				currentTexCoord = texCoords.get(currentIndex.texCoordIndex);
			
			if (hasNormals)
				currentNormal = normals.get(currentIndex.normalIndex);
			
			Integer modelVertexIndex = resultIndexMap.get(currentIndex);
			
			if (modelVertexIndex == null) {
				modelVertexIndex = res.getPositions().size();
				resultIndexMap.put(currentIndex, modelVertexIndex);
				
				res.getPositions().add(currentPosition);
				res.getTexCoords().add(currentTexCoord);
				if (hasNormals)
					res.getNormals().add(currentNormal);
			} 
			
			Integer normalModelIndex = normalIndexMap.get(currentIndex.vertexIndex);
			
			if (normalModelIndex == null) {
				normalModelIndex = normalModel.getPositions().size();
				normalIndexMap.put(currentIndex.vertexIndex, normalModelIndex);
				
				normalModel.getPositions().add(currentPosition);
				normalModel.getTexCoords().add(currentTexCoord);
				normalModel.getNormals().add(currentNormal);
				normalModel.getTangents().add(new Vector3f(0,0,0));
			}
			
			res.getIndices().add(modelVertexIndex);
			normalModel.getIndices().add(normalModelIndex);
			indexMap.put(modelVertexIndex, normalModelIndex);
		}
		
		if (!hasNormals) {
			normalModel.calcNormals();
			
			for (int i = 0; i < res.getPositions().size(); i++) {
				res.getNormals().add(normalModel.getNormals().get(indexMap.get(i)));
			}
		}
		
		normalModel.calcTangents();

		for(int i = 0; i < res.getPositions().size(); i++)
			res.getTangents().add(normalModel.getTangents().get(indexMap.get(i)));
		
		for(int i = 0; i < res.getTexCoords().size(); i++)
			res.getTexCoords().get(i).setY(1.0f - res.getTexCoords().get(i).getY());
		
		return res;
	}
	
	private OBJIndex parseOBJIndex(String token) {
		String[] values = token.split("/");
		
		OBJIndex result = new OBJIndex();
		result.vertexIndex = Integer.parseInt(values[0]) - 1;
		
		if (values.length > 1) {
			
			if(!values[1].isEmpty()) {
				hasTexCoords = true;
				result.texCoordIndex = Integer.parseInt(values[1]) - 1;
			}
			if (values.length > 2) {
				hasNormals = true;
				result.normalIndex = Integer.parseInt(values[2]) - 1;
			}
		}
		return result;
	}
}
