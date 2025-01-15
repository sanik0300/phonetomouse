using System.Windows;

namespace DesktopClient
{
    public partial class CloseConfirmationWindow : Window
    {
        public CloseConfirmationWindow() => InitializeComponent();

        private void noClosureButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            this.DataContext = false; //aka value returned on close
            this.Close();
        }

        private void yesCloseButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            this.DataContext = true;
            this.Close();
        }

        private void Window_Loaded(object sender, System.Windows.RoutedEventArgs e) => WindowAppearanceExtensions.Remove3SystemButtons(this);
        
    }
}
