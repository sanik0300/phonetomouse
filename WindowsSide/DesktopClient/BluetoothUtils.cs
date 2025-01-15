using InTheHand.Net.Sockets;
using MouseSimTestEnvironment;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Printing.IndexedProperties;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Windows.Threading;

namespace DesktopClient
{
    public class BluetoothUtils
    {
        private const int maxDevices = 255, screenSizeAsForCpp = 65535;
        private bool listenerStarted = false;
        public bool ListenerStarted { get { return listenerStarted; } }
        private bool connectedToPhone = false;

        IList<BluetoothDeviceInfo> visibles = null;
        IList<BluetoothDeviceInfo> paired = null;
        private BluetoothDeviceInfo selectedForListening = null;

        BluetoothClient _clientForDiscovery = new BluetoothClient(),
                        _clientOfSecondDevice=null;
        BluetoothListener _listener;
        public AsyncCallback OnDiscoverVisibleCompleted, OnDiscoverPairedCompleted,
                             OnClientFound;
        public event EventHandler OnDisconnected;
        public event EventHandler<WaitStartEventArgs> OnStartedWaitingForConnection;

        private CanvasDrawer _drawer;

        public BluetoothUtils(CanvasDrawer drawer)
        {
            this._drawer = drawer;

            string guidStr = File.ReadAllText("guid.txt");
            _listener = new BluetoothListener(Guid.Parse(guidStr));
            
            OnDiscoverPairedCompleted = new AsyncCallback(this.assignPaired);
            OnDiscoverVisibleCompleted = new AsyncCallback(this.assignVisible);

            OnClientFound = new AsyncCallback((result) =>
            {
                _clientOfSecondDevice = _listener.EndAcceptBluetoothClient(result);
                connectedToPhone = true;

                SendScreenDimensions(_clientOfSecondDevice);

                Thread _listeningThread = new Thread(new ParameterizedThreadStart(ListenAndCatchData));

                _listeningThread.Start(_clientOfSecondDevice);
            });
        }


        private void SendScreenDimensions(object stream)
        {
            int[] dimensions = new int[2] { Screen.PrimaryScreen.Bounds.Width, Screen.PrimaryScreen.Bounds.Height };
            byte[] arrBeingSent = new byte[dimensions.Length*sizeof(int)+1];
            arrBeingSent[0] = (byte)MouseCommands.Ratio;
            Buffer.BlockCopy(dimensions, 0, arrBeingSent, 1, arrBeingSent.Length-1);

            BluetoothClient client = stream as BluetoothClient;
            client.GetStream().WriteAsync(arrBeingSent, 0, arrBeingSent.Length);
        }

        public void Disconnect(bool sendResponse, bool fireEvent)
        {
            if (sendResponse && _clientOfSecondDevice != null)
            {
                _clientOfSecondDevice.GetStream().WriteAsync(new byte[] { (byte)MouseCommands.Disconnect });
            }
            _listener.Stop();
            connectedToPhone = listenerStarted = false;

            if (fireEvent)
            {
                OnDisconnected.Invoke(this, EventArgs.Empty);
            }
        }

        private void ListenAndCatchData(object obj)
        {
            BluetoothClient cl = obj as BluetoothClient;
            bool leftBtn = true;
            float xPercent = 0, yPercent = 0;
            while (connectedToPhone)
            {

                int firstByteVal = cl.GetStream().ReadByte();
                if(firstByteVal == -1) { continue; }

                byte[] receiveBuffer;
                switch((MouseCommands)firstByteVal)
                {
                    case MouseCommands.Wheel:
                        receiveBuffer = new byte[2];
                        break;
                    case MouseCommands.Disconnect:
                        receiveBuffer = new byte[3];
                        break;
                    default:
                        receiveBuffer = new byte[10];
                        break;
                    
                }
                receiveBuffer[0] = (byte)firstByteVal;
                cl.GetStream().Read(receiveBuffer, 1, receiveBuffer.Length - 1);

                singleEventProcessing(receiveBuffer, ref leftBtn, ref xPercent, ref yPercent);
            }
        }

#if DEBUG
        private void debugShowArray(byte[] arr)
        {
            Debug.Write("[ ");
            foreach(byte b in arr)
            {
                Debug.Write($"{b} ");
            }
            Debug.WriteLine(']');
        }
#endif

        private void singleEventProcessing(byte[] buffer, ref bool leftBtn, ref float xPercent, ref float yPercent)
        {
            if (buffer[0] <= 4)
            {
                leftBtn = Convert.ToBoolean(buffer[1]);
                xPercent = BitConverter.ToSingle(buffer, 2);
                yPercent = BitConverter.ToSingle(buffer, 6);
            }
            int xWinapi = (int)(xPercent*InputSimWrapper.ScreenAnyAxisMax),
                yWinapi = (int)(yPercent*InputSimWrapper.ScreenAnyAxisMax);
            
            #if DEBUG
                debugShowArray(buffer);
            #endif

            switch ((MouseCommands)buffer[0])
            {
                case MouseCommands.Disconnect:
                    this.Disconnect(Convert.ToBoolean(buffer[1]), true);
                    if(Properties.Settings.Default.WaitForReconnect && !Convert.ToBoolean(buffer[2]))
                    {
                        StartWaitingForConnection(true);
                    }
                    break;
                case MouseCommands.Move:

                    byte buttonByte = buffer[1];

                    InputSimWrapper.mouse_move(xWinapi, yWinapi);

                    if(buttonByte > 1)
                    {
                        _drawer.DrawOnlyPoint(xPercent, yPercent, null);
                    }
                    else {
                        _drawer.DrawPolylineSegment(xPercent, yPercent);
                    }
                    break;
                case MouseCommands.Click:
                    { 
                        if(leftBtn) {
                            InputSimWrapper.left_click(xWinapi, yWinapi);
                        }
                        else {
                            InputSimWrapper.right_click(xWinapi, yWinapi);
                        }
                        _drawer.DrawOnlyPoint(xPercent, yPercent, leftBtn);
                    }
                    break;
                case MouseCommands.DoubleClick:
                    {
                        for(byte i = 0; i<2; i++)
                        {
                            if (leftBtn) {
                                InputSimWrapper.left_click(xWinapi, yWinapi);
                            }
                            else {
                                InputSimWrapper.right_click(xWinapi, yWinapi);
                            }
                        }
                        _drawer.DrawOnlyPoint(xPercent, yPercent, leftBtn);
                    }
                    break;
                case MouseCommands.Down:
                    //Debug.Write("mouse Down");
                    if(leftBtn) {
                        InputSimWrapper.left_key_down(xWinapi, yWinapi);
                    }
                    else {
                        InputSimWrapper.right_key_down(xWinapi, yWinapi);
                    }  
                    _drawer.DrawOnlyPoint(xPercent, yPercent, leftBtn);
                    _drawer.BeginPolyline(xPercent, yPercent, leftBtn);
                    break;
                case MouseCommands.Up:
                    //Debug.Write("mous up");
                    if (leftBtn) {
                        InputSimWrapper.left_key_up(xWinapi, yWinapi);
                    }
                    else {
                        InputSimWrapper.right_key_up(xWinapi, yWinapi);
                    }
                    _drawer.DrawOnlyPoint(xPercent, yPercent, null);
                    break;
                case MouseCommands.Wheel:
                    {
                        bool scrollUp = Convert.ToBoolean(buffer[1]);

                        if (scrollUp) {
                            InputSimWrapper.wheel_forward(1);
                        }
                        else {
                            InputSimWrapper.wheel_backward(1);
                        }
                        _drawer.WriteWheelScrollInfo(scrollUp);
                    }
                    break;
            }

            if (buffer[0]>0 && buffer[0] < 5)
            {
                _drawer.WriteButtonGestureInfo((MouseCommands)buffer[0], xPercent, yPercent, leftBtn);
            }
        }

        public IEnumerable<string> VisibleDevicesNames
        {
            get { return visibles.Select(x => x.DeviceName); }
        }
        public IEnumerable<string> PairedDevicesNames
        {
            get { return paired.Select(x => x.DeviceName); }
        }

        private void assignVisible(IAsyncResult result) => visibles = _clientForDiscovery.EndDiscoverDevices(result);

        private void assignPaired(IAsyncResult result) => paired = _clientForDiscovery.EndDiscoverDevices(result);


        public void DiscoverAll()
        {
            _clientForDiscovery.BeginDiscoverDevices(maxDevices, false, false, true, true, OnDiscoverVisibleCompleted, null);
            _clientForDiscovery.BeginDiscoverDevices(maxDevices, true, false, false, false, OnDiscoverPairedCompleted, null);
        }
        public void DiscoverVisible()
        {
            _clientForDiscovery.BeginDiscoverDevices(maxDevices, false, false, true, true, OnDiscoverVisibleCompleted, null);
        }
        public void DiscoverPaired()
        {
            _clientForDiscovery.BeginDiscoverDevices(maxDevices, true, false, false, false, OnDiscoverPairedCompleted, null);
        }

        public void CancelDiscoveryOfAll() => _clientForDiscovery.EndDiscoverDevices(null);

        public void SelectDevice(int index) => selectedForListening = paired[index];

        public void StartWaitingForConnection(bool waitForReconnect = false)
        {
            if (!listenerStarted)
            {
                _listener.Start();
                listenerStarted = true;
            }
            OnStartedWaitingForConnection?.Invoke(this, new WaitStartEventArgs(waitForReconnect));
            _listener.BeginAcceptBluetoothClient(OnClientFound, null);
        }
    }
}
