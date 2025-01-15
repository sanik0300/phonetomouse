using System;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;

namespace DesktopClient
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        BluetoothUtils bluetoothUtilsInstance;
        CloseConfirmationWindow linkedCloseConfirmWindow = null;
        bool closeConfirmed = false;    
        public MainWindow()
        {
            InitializeComponent();
            panel2_SizeChanged(this, null);

            bluetoothUtilsInstance = new BluetoothUtils(new CanvasDrawer(canvas, writeGesturesHereTxt));

            bluetoothUtilsInstance.OnDiscoverVisibleCompleted += (result) =>
            {
                this.Dispatcher.Invoke(() =>
                {
                    visibleListBox.ItemsSource = bluetoothUtilsInstance.VisibleDevicesNames;
                    adjustButtons(true);
                    showLoading(stackV, false);
                });
            };
            bluetoothUtilsInstance.OnDiscoverPairedCompleted += (result) =>
            {
                this.Dispatcher.Invoke(() =>
                {
                    pairedListBox.ItemsSource = bluetoothUtilsInstance.PairedDevicesNames;
                    adjustButtons(true);
                    showLoading(stackP, false);
                });
            };
            bluetoothUtilsInstance.OnStartedWaitingForConnection += (s, args) =>
            {
                this.Dispatcher.Invoke(() =>
                {
                    waitStatusText.Text = args.ForReconnect ? "connection ended for technical reasons, waiting to reconnect" :
                                                             "waiting for new connection";
                });
            };
            bluetoothUtilsInstance.OnClientFound += (result) =>
            {
                this.Dispatcher.Invoke(() => 
                { 
                    canvas.Background = Brushes.White;
                    closeConnectionBtn.IsEnabled = true;
                    waitStatusText.Text = string.Empty;

                    if(Properties.Settings.Default.AutoMinimizeOnConnect)
                    {
                        this.WindowState = WindowState.Minimized;
                    }
                });
            };
            bluetoothUtilsInstance.OnDisconnected += (s, a) =>
            {
                this.Dispatcher.Invoke(() =>
                {
                    canvas.Background = Brushes.Gray;
                    canvas.Children.Clear();
                    openConnectionBtn.IsEnabled = true;
                    closeConnectionBtn.IsEnabled = false;

                    if(Properties.Settings.Default.AutoMaximizeOnDisconnect)
                    {
                        this.WindowState = WindowState.Normal;   
                    }
                });
            };
        }

        private void adjustButtons(bool completed)
        {
            foreach (UIElement child in discoverBtnsPanel.Children) { child.IsEnabled = completed; }
            //cancelDiscBtn.IsEnabled = !completed;
        }
        private void showLoading(StackPanel container, bool show)
        {
            ListBox lb = container.Children.OfType<ListBox>().First();
            MediaElement img = container.Children.OfType<MediaElement>().First();

            img.Visibility = show ? Visibility.Visible : Visibility.Collapsed;
            lb.Visibility = show ? Visibility.Collapsed : Visibility.Visible;
        }

        private void discVisibleBtn_Click(object sender, RoutedEventArgs e)
        {
            adjustButtons(false);
            showLoading(stackV, true);

            bluetoothUtilsInstance.DiscoverVisible();
        }

        private void discAllBtn_Click(object sender, RoutedEventArgs e)
        {
            adjustButtons(false);
            showLoading(stackV, true);
            showLoading(stackP, true);

            bluetoothUtilsInstance.DiscoverAll();
        }

        private void discPairedBtn_Click(object sender, RoutedEventArgs e)
        {
            adjustButtons(false);
            showLoading(stackP, true);

            bluetoothUtilsInstance.DiscoverPaired();
        }

        private void cancelDiscBtn_Click(object sender, RoutedEventArgs e)
        {
            adjustButtons(true);
            showLoading(stackP, false);
            showLoading(stackV, false);
        }

        private void openConnectionBtn_Click(object sender, RoutedEventArgs e)
        {
            openConnectionBtn.IsEnabled = false;
            bluetoothUtilsInstance.StartWaitingForConnection();
        }

        private void MediaElement_MediaEnded(object sender, RoutedEventArgs e)
        {
            (sender as MediaElement).Position = new TimeSpan(0, 0, 0, 0, 1);
            (sender as MediaElement).Play();
        }

        private void pairedListBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            openConnectionBtn.IsEnabled = true;
            selectedPairedDeviceTxt.Text = (sender as ListBox).SelectedItem.ToString();
            bluetoothUtilsInstance.SelectDevice((sender as ListBox).SelectedIndex);
        }

        private void closeConnectionBtn_Click(object sender, RoutedEventArgs e)
        {
            bluetoothUtilsInstance.Disconnect(true, true);
        }

        private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            if(closeConfirmed) { return; }

            if(linkedCloseConfirmWindow == null)
            {
                linkedCloseConfirmWindow = new CloseConfirmationWindow();
                linkedCloseConfirmWindow.Show();

                linkedCloseConfirmWindow.Closed += AppReallyClosing;
            }
            else {
                linkedCloseConfirmWindow.Focus();
            }

            e.Cancel = true;
        }

        private void AppReallyClosing(object? sender, EventArgs e)
        {
            closeConfirmed = (bool)linkedCloseConfirmWindow.DataContext;

            linkedCloseConfirmWindow.Closed -= AppReallyClosing;
            linkedCloseConfirmWindow = null;

            if(!closeConfirmed) { return; } 

            if (bluetoothUtilsInstance.ListenerStarted)
            {
                bluetoothUtilsInstance.Disconnect(true, false);
            }
            this.Close();
        }

        private void panel2_SizeChanged(object sender, SizeChangedEventArgs e)
        {
            Point relativeLocation = setting1text.TranslatePoint(default, settingPanel);

            setting1text.Width = panel2.ActualWidth - relativeLocation.X;
            if(e==null || !e.WidthChanged) { return; }
            
            int actualHeightsSum = (int)panel2.Children.Cast<FrameworkElement>().Where(el => !(el is Border)).Sum(el => el.ActualHeight);
            this.MinHeight = actualHeightsSum + canvasBdr.ActualHeight;
        }

        private void settingFromCheckbox(Action<bool> setter, object sender)
        {
            setter((sender as CheckBox).IsChecked.Value);
            Properties.Settings.Default.Save();
        }

        private void reconnectSetting_CheckedChanged(object sender, RoutedEventArgs e) => settingFromCheckbox((val) => Properties.Settings.Default.WaitForReconnect = val, sender);

        private void connectMiniCheck_CheckedChanged(object sender, RoutedEventArgs e) => settingFromCheckbox((val) => Properties.Settings.Default.AutoMinimizeOnConnect = val, sender);

        private void disconnectMaxiCheck_CheckedChanged(object sender, RoutedEventArgs e) => settingFromCheckbox((val) => Properties.Settings.Default.AutoMaximizeOnDisconnect = val, sender);
    }
}
