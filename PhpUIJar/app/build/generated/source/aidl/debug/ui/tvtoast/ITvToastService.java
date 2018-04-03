/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: P:\\philips\\philips_ui_jar\\PhpUIJar\\app\\src\\main\\aidl\\ui\\tvtoast\\ITvToastService.aidl
 */
package ui.tvtoast;
// Declare any non-default types here with import statements

public interface ITvToastService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements ui.tvtoast.ITvToastService
{
private static final java.lang.String DESCRIPTOR = "ui.tvtoast.ITvToastService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an ui.tvtoast.ITvToastService interface,
 * generating a proxy if needed.
 */
public static ui.tvtoast.ITvToastService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof ui.tvtoast.ITvToastService))) {
return ((ui.tvtoast.ITvToastService)iin);
}
return new ui.tvtoast.ITvToastService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_showTvToastMessage:
{
data.enforceInterface(DESCRIPTOR);
android.os.IBinder _arg0;
_arg0 = data.readStrongBinder();
ui.tvtoast.TvToast _arg1;
if ((0!=data.readInt())) {
_arg1 = ui.tvtoast.TvToast.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.showTvToastMessage(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_cancelTvToastMessage:
{
data.enforceInterface(DESCRIPTOR);
android.os.IBinder _arg0;
_arg0 = data.readStrongBinder();
ui.tvtoast.TvToast _arg1;
if ((0!=data.readInt())) {
_arg1 = ui.tvtoast.TvToast.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.cancelTvToastMessage(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements ui.tvtoast.ITvToastService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
	* show Tv Toast
	*/
@Override public void showTvToastMessage(android.os.IBinder contextToken, ui.tvtoast.TvToast msg) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder(contextToken);
if ((msg!=null)) {
_data.writeInt(1);
msg.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_showTvToastMessage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	* cancel Tv Toast
	*/
@Override public void cancelTvToastMessage(android.os.IBinder contextToken, ui.tvtoast.TvToast msg) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder(contextToken);
if ((msg!=null)) {
_data.writeInt(1);
msg.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_cancelTvToastMessage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_showTvToastMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_cancelTvToastMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
/**
	* show Tv Toast
	*/
public void showTvToastMessage(android.os.IBinder contextToken, ui.tvtoast.TvToast msg) throws android.os.RemoteException;
/**
	* cancel Tv Toast
	*/
public void cancelTvToastMessage(android.os.IBinder contextToken, ui.tvtoast.TvToast msg) throws android.os.RemoteException;
}
