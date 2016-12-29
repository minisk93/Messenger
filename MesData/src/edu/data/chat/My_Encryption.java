package edu.data.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;


public class My_Encryption 
{
	private static final byte[] key = "negstcerb1234567".getBytes();// ���� ���������� (�� ������ 16-�� ��������)
	private static final String transform = "AES";

	public static void encrypt(MessageData object,OutputStream outStream)//����� �������� ������
	{
		//������� ������������ �����
		SecretKeySpec sks = new SecretKeySpec(key,transform);
		//������� ����
		try 
		{
			Cipher cipher = Cipher.getInstance(transform);
			cipher.init(Cipher.ENCRYPT_MODE,sks);
			//������� ����������� ������
			SealedObject so = new SealedObject(object,cipher);
			
			ObjectOutputStream oos = new ObjectOutputStream(new CipherOutputStream(outStream,cipher));
			oos.writeObject(so);
			oos.close();//��� ����.outputstream ����� �������� close(),� �� flush()!!!!!
		
		} 
		catch (NoSuchAlgorithmException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (NoSuchPaddingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidKeyException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IllegalBlockSizeException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Object decrypt(InputStream is) throws ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, IOException//����� �����������
	{
		SecretKeySpec sks = new SecretKeySpec(key,transform);
		Cipher cipher=null;
		SealedObject so=null;
		
		try 
		{
			cipher = Cipher.getInstance(transform);
			cipher.init(Cipher.DECRYPT_MODE,sks);
			
			ObjectInputStream ois = new ObjectInputStream(new CipherInputStream(is,cipher));
		
			so = (SealedObject)ois.readObject();
			
		} 
		catch (NoSuchAlgorithmException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (NoSuchPaddingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InvalidKeyException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return so.getObject(cipher);
	}
}

